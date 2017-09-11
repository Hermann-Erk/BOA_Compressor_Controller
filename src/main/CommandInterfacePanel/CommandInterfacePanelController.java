package main.CommandInterfacePanel;

import main.Constants;
import main.SerialConnection.CommandSenderInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Hermann on 10.09.2017.
 */
public class CommandInterfacePanelController {
    private CommandInterfacePanelForm commandInterfacePanelForm;
    private CommandSenderInterface commandSenderFront;
    private CommandSenderInterface commandSenderBack;

    public CommandInterfacePanelController(CommandInterfacePanelForm form, CommandSenderInterface frontSender,
                                           CommandSenderInterface backSender){
        this.commandInterfacePanelForm = form;
        this.commandSenderFront = frontSender;
        this.commandSenderBack = backSender;

        this.commandInterfacePanelForm.absMoveButton.addActionListener(e -> sendPositionCommand(e.getSource()));
        this.commandInterfacePanelForm.relMoveButton.addActionListener(e -> sendPositionCommand(e.getSource()));
        this.commandInterfacePanelForm.measureStageButton.addActionListener(e -> sendPositionCommand(e.getSource()));
        this.commandInterfacePanelForm.referenceToZeroButton.addActionListener(e -> sendPositionCommand(e.getSource()));

        this.commandInterfacePanelForm.stopButton.addActionListener(e -> stopMotors());
    }

    private void stopMotors(){
        try {
            commandSenderFront.sendCommand("1" + Constants.STOP_CMD);
            commandSenderFront.sendCommand("2" + Constants.STOP_CMD);
        } catch (Exception e){
            System.out.println("WARNING: Stop commands were not send to the front Arduino.");
        }

        try {
            commandSenderBack.sendCommand("1" + Constants.STOP_CMD);
            commandSenderBack.sendCommand("2" + Constants.STOP_CMD);
        } catch (Exception e){
            System.out.println("WARNING: Stop commands were not send to the back Arduino.");
        }

    }

    private void sendPositionCommand(Object eventObject){
        String motorString = "";
        CommandSenderInterface sender = null;

        switch(this.commandInterfacePanelForm.comboBox1.getSelectedIndex()){
            case 0:
                motorString = "1";
                sender = commandSenderFront;
                break;
            case 1:
                motorString = "2";
                sender = commandSenderFront;
                break;
            case 2:
                motorString = "1";
                sender = commandSenderBack;
                break;
            case 3:
                motorString = "2";
                sender = commandSenderBack;
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
                }else{
                    if(eventObject == this.commandInterfacePanelForm.measureStageButton){
                        commandAbbreviation = Constants.MEASURE_STAGE_CMD;
                    }
                }
            }
        }

        int position = 0;
        if(!(commandAbbreviation == Constants.MEASURE_STAGE_CMD || commandAbbreviation == Constants.REFERENCE_CMD)){
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
        return;
    }
}
