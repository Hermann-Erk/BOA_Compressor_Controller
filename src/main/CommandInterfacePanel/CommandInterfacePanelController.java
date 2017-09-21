package main.CommandInterfacePanel;

import main.Constants;
import main.Motor;
import main.ObserversAndListeners.ArduinoResponseListener;
import main.SerialConnection.CommandSenderConnected;
import main.SerialConnection.CommandSenderInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Observable;

/**
 * Created by Hermann on 10.09.2017.
 */
public class CommandInterfacePanelController extends Observable implements CommandSenderConnected, ArduinoResponseListener {
    private CommandInterfacePanelForm commandInterfacePanelForm;
    private CommandSenderInterface commandSenderFront;
    private CommandSenderInterface commandSenderBack;

    private boolean frontCornerCubeIsMoving = false;
    private boolean backCornerCubeIsMoving = false;
    private boolean frontRoofmirrorIsMoving = false;
    private boolean backRoofmirrorIsMoving = false;

    public CommandInterfacePanelController(CommandInterfacePanelForm form, CommandSenderInterface frontSender,
                                           CommandSenderInterface backSender){
        this.commandInterfacePanelForm = form;
        this.commandSenderFront = frontSender;
        this.commandSenderBack = backSender;

        this.commandInterfacePanelForm.absMoveButton.addActionListener(e -> sendPositionCommand(e.getSource()));
        this.commandInterfacePanelForm.relMoveButton.addActionListener(e -> sendPositionCommand(e.getSource()));
        this.commandInterfacePanelForm.measureStageButton.addActionListener(e -> sendPositionCommand(e.getSource()));
        this.commandInterfacePanelForm.referenceToZeroButton.addActionListener(e -> sendPositionCommand(e.getSource()));

        this.commandInterfacePanelForm.comboBox1.addActionListener(e -> enableAndDisableComponents(e));

        this.commandInterfacePanelForm.stopButton.addActionListener(e -> stopMotors());
    }

    private void enableAndDisableComponents(ActionEvent event){
        JComboBox comboBox = (JComboBox) event.getSource();
        switch(comboBox.getSelectedIndex()){
            case 0:
                if(this.frontCornerCubeIsMoving){
                    disableButtons();
                }else{
                    enableButtons();
                }
                break;
            case 1:
                if(this.frontRoofmirrorIsMoving){
                    disableButtons();
                }else{
                    enableButtons();
                }
                break;
            case 2:
                if(this.backCornerCubeIsMoving){
                    disableButtons();
                }else{
                    enableButtons();
                }
                break;
            case 3:
                if(this.backRoofmirrorIsMoving){
                    disableButtons();
                }else{
                    enableButtons();
                }
                break;
        }
    }

    private void disableButtons(){
        this.commandInterfacePanelForm.relMoveButton.setEnabled(false);
        this.commandInterfacePanelForm.absMoveButton.setEnabled(false);
        this.commandInterfacePanelForm.referenceToZeroButton.setEnabled(false);
        this.commandInterfacePanelForm.measureStageButton.setEnabled(false);
    }

    private void enableButtons(){
        this.commandInterfacePanelForm.relMoveButton.setEnabled(true);
        this.commandInterfacePanelForm.absMoveButton.setEnabled(true);
        this.commandInterfacePanelForm.referenceToZeroButton.setEnabled(true);
        this.commandInterfacePanelForm.measureStageButton.setEnabled(true);
    }

    private void stopMotors(){
        commandSenderFront.sendCommand("2" + Constants.STOP_CMD);
        commandSenderFront.sendCommand("1" + Constants.STOP_CMD);
        commandSenderBack.sendCommand("2" + Constants.STOP_CMD);
        commandSenderBack.sendCommand("1" + Constants.STOP_CMD);
    }

    private void sendPositionCommand(Object eventObject){
        String motorString = "";
        CommandSenderInterface sender = null;
        String arduino = "";

        switch(this.commandInterfacePanelForm.comboBox1.getSelectedIndex()){
            case 0:
                motorString = "1";
                sender = commandSenderFront;
                arduino = "front";
                break;
            case 1:
                motorString = "2";
                sender = commandSenderFront;
                arduino = "front";
                break;
            case 2:
                motorString = "1";
                sender = commandSenderBack;
                arduino = "back";
                break;
            case 3:
                motorString = "2";
                sender = commandSenderBack;
                arduino = "back";
                break;
        }

        String commandAbbreviation = "";

        if(eventObject == this.commandInterfacePanelForm.absMoveButton){
            commandAbbreviation = Constants.ABS_MOVE_CMD;
        }else{
            if(eventObject == this.commandInterfacePanelForm.relMoveButton){
                commandAbbreviation = Constants.REL_MOVE_CMD;
            }else{
                if(eventObject == this.commandInterfacePanelForm.referenceToZeroButton){
                    commandAbbreviation = Constants.REFERENCE_CMD;
                    setChanged();
                    notifyObservers(Motor.getAbbreviation(arduino, (Integer.parseInt(motorString) - 1) + ""));
                }else{
                    if(eventObject == this.commandInterfacePanelForm.measureStageButton){
                        commandAbbreviation = Constants.MEASURE_STAGE_CMD;
                    }
                }
            }
        }

        int position = 0;
        if(!(commandAbbreviation.equals(Constants.MEASURE_STAGE_CMD) || commandAbbreviation.equals(Constants.REFERENCE_CMD))){
            try {
                position = Integer.parseInt(this.commandInterfacePanelForm.positionValueField.getText());
            } catch (NumberFormatException exc) {
                System.out.println("WARNING: Text field content could not be converted into a number.");
                return;
            }
        }

        try {
            sender.sendCommand(motorString + commandAbbreviation + position);
        }catch (Exception exc){
            System.out.println("WARNING: Command " + motorString + commandAbbreviation + position +
                    " has not been sent!");
        }
    }

    public void setNewCommandSenders(CommandSenderInterface frontSender, CommandSenderInterface backSender){
        this.commandSenderFront = frontSender;
        this.commandSenderBack = backSender;
    }

    @Override
    public void arduinoResponse(Motor motor, String command, int value) {
        switch (command){
            case Constants.ARDUINO_STARTED_MOVING_RESPONSE:
                switch (motor){
                    case VC:
                        this.frontCornerCubeIsMoving = true;
                            if (this.commandInterfacePanelForm.comboBox1.getSelectedIndex() == 0) {
                                disableButtons();
                            }
                        break;
                    case VR:
                        this.frontRoofmirrorIsMoving = true;
                            if (this.commandInterfacePanelForm.comboBox1.getSelectedIndex() == 1) {
                                disableButtons();
                            }
                        break;
                    case HC:
                        this.backCornerCubeIsMoving = true;
                            if (this.commandInterfacePanelForm.comboBox1.getSelectedIndex() == 2) {
                                disableButtons();
                            }
                        break;
                    case HR:
                        this.backRoofmirrorIsMoving = true;
                            if (this.commandInterfacePanelForm.comboBox1.getSelectedIndex() == 3) {
                                disableButtons();
                            }
                        break;
                }
                break;
            case Constants.ARDUINO_STOPPED_MOVING_RESPONSE:
                switch (motor){
                    case VC:
                        this.frontCornerCubeIsMoving = false;
                            if (this.commandInterfacePanelForm.comboBox1.getSelectedIndex() == 0) {
                                enableButtons();
                            }
                        break;
                    case VR:
                        this.frontRoofmirrorIsMoving = false;
                            if (this.commandInterfacePanelForm.comboBox1.getSelectedIndex() == 1) {
                                enableButtons();
                            }
                        break;
                    case HC:
                        this.backCornerCubeIsMoving = false;
                            if (this.commandInterfacePanelForm.comboBox1.getSelectedIndex() == 2) {
                                enableButtons();
                            }
                        break;
                    case HR:
                        this.backRoofmirrorIsMoving = false;
                            if (this.commandInterfacePanelForm.comboBox1.getSelectedIndex() == 3) {
                                enableButtons();
                            }
                        break;
                }
                break;
        }
    }
}
