package main.CalibrationControlPanel;

import main.CalibrationData.CalibrationCalculator;
import main.CalibrationData.CalibrationFileParser;
import main.CalibrationData.PositionSet;
import main.Constants;
import main.Motor;
import main.ObserversAndListeners.ArduinoResponseListener;
import main.SerialConnection.CommandSenderConnected;
import main.SerialConnection.CommandSenderInterface;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Hermann on 13.09.2017.
 */
public class CalibrationPanelController implements CommandSenderConnected, ArduinoResponseListener{
    private CalibrationPanelForm calibrationPanelForm;
    private CommandSenderInterface commandSenderFront;
    private CommandSenderInterface commandSenderBack;
    private ArrayList<int[]> calibrationData2H;
    private ArrayList<int[]> calibrationData3H;
    private CalibrationFileParser parser = new CalibrationFileParser();

    public CalibrationPanelController(CalibrationPanelForm calibrationPanelForm, CommandSenderInterface commandSenderFront,
                                      CommandSenderInterface commandSenderBack){
        this.calibrationPanelForm = calibrationPanelForm;
        this.commandSenderFront = commandSenderFront;
        this.commandSenderBack = commandSenderBack;

        // Load up the calibration files, disable components if they are not found
        try {
            this.calibrationData2H = parser.readCalibrationFile(Constants.calibrationFilePath2H);
        }catch(IOException e){
            System.out.println("WARNING: Could not find calibration file for the 2H NOPA.");
            this.calibrationPanelForm.setWavelength2HButton.setEnabled(false);
            this.calibrationPanelForm.setBothNOPAsCheckBox.setEnabled(false);
            this.calibrationPanelForm.setBothNOPAsCheckBox.setSelected(false);
        }

        try {
            this.calibrationData3H = parser.readCalibrationFile(Constants.calibrationFilePath3H);
        }catch(IOException e){
            System.out.println("WARNING: Could not find calibration file for the 3H NOPA.");
            this.calibrationPanelForm.setWavelength3HButton.setEnabled(false);
            this.calibrationPanelForm.setBothNOPAsCheckBox.setEnabled(false);
            this.calibrationPanelForm.setBothNOPAsCheckBox.setSelected(false);
        }

        this.calibrationPanelForm.setWavelength2HButton.addActionListener(e -> setMotorsAccordingToCalibration(e));
        this.calibrationPanelForm.setWavelength3HButton.addActionListener(e -> setMotorsAccordingToCalibration(e));
    }

    private void setMotorsAccordingToCalibration(ActionEvent event) {
        CalibrationCalculator calCalculator = new CalibrationCalculator(calibrationData2H, calibrationData3H);

        int wavelength2H = -1;
        int wavelength3H = -1;

        try {
            if (this.calibrationPanelForm.setBothNOPAsCheckBox.isSelected()) {
                wavelength2H = Integer.parseInt(this.calibrationPanelForm.textField2H.getText());
                wavelength3H = Integer.parseInt(this.calibrationPanelForm.textField3H.getText());
            } else {
                if (event.getSource() == this.calibrationPanelForm.setWavelength2HButton) {
                    wavelength2H = Integer.parseInt(this.calibrationPanelForm.textField2H.getText());
                }
                if (event.getSource() == this.calibrationPanelForm.setWavelength3HButton) {
                    wavelength3H = Integer.parseInt(this.calibrationPanelForm.textField3H.getText());
                }
            }
        } catch (Exception e) {
            System.out.println("WARNING: Text fields must contain valid integers.");
        }

        PositionSet positions = calCalculator.calculatePosition(wavelength2H, wavelength3H);

        if (positions.getCornerCubeFrontPos() != -1) {
            commandSenderFront.sendCommand("2" + Constants.ABS_MOVE_CMD + positions.getRoofMirrorFrontPos());
            // workaround for the arduino to recognise and accept both commands TODO check if this can be done better in the arduino program
            commandSenderFront.sendCommand("1" + Constants.ABS_MOVE_CMD + positions.getCornerCubeFrontPos());
            commandSenderFront.sendCommand("1" + Constants.ABS_MOVE_CMD + positions.getCornerCubeFrontPos());

        }

        if (positions.getCornerCubeBackPos() != -1) {
            commandSenderBack.sendCommand("2" + Constants.ABS_MOVE_CMD + positions.getGetRoofMirrorBackPos());
            commandSenderBack.sendCommand("1" + Constants.ABS_MOVE_CMD + positions.getCornerCubeBackPos());
            commandSenderBack.sendCommand("1" + Constants.ABS_MOVE_CMD + positions.getCornerCubeBackPos());
            System.out.println("Stages are being set for the given wavelength.");
        }
    }



    public void setNewCommandSenders(CommandSenderInterface commandSenderFront, CommandSenderInterface commandSenderBack){
        this.commandSenderFront = commandSenderFront;
        this.commandSenderBack = commandSenderBack;
    }

    @Override
    public void arduinoResponse(Motor motor, String command, int value) {
        switch (command){
            case Constants.ARDUINO_STARTED_MOVING_RESPONSE:
                switch(motor){
                    case VC:
                    case VR:
                        this.calibrationPanelForm.setWavelength2HButton.setEnabled(false);
                        this.calibrationPanelForm.setBothNOPAsCheckBox.setEnabled(false);
                        break;
                    case HC:
                    case HR:
                        this.calibrationPanelForm.setWavelength3HButton.setEnabled(false);
                        this.calibrationPanelForm.setBothNOPAsCheckBox.setEnabled(false);
                        break;
                }
                break;
            case Constants.ARDUINO_STOPPED_MOVING_RESPONSE:
                switch(motor){
                    case VC:
                    case VR:
                        if(this.calibrationData2H != null) {
                            this.calibrationPanelForm.setWavelength2HButton.setEnabled(true);
                            this.calibrationPanelForm.setBothNOPAsCheckBox.setEnabled(true);
                            break;
                        }
                    case HC:
                    case HR:
                        if(this.calibrationData3H != null) {
                            this.calibrationPanelForm.setWavelength3HButton.setEnabled(true);
                            this.calibrationPanelForm.setBothNOPAsCheckBox.setEnabled(true);
                            break;
                        }
                }
                break;
            default:
                break;
        }
    }
}
