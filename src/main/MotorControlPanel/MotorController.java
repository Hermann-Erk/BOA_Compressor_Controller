package main.MotorControlPanel;

import main.Constants;
import main.Motor;
import main.ObserversAndListeners.ArduinoResponseListener;
import main.SerialConnection.CommandSender;
import main.SerialConnection.CommandSenderInterface;

import static main.Motor.getAbbreviation;

/**
 * Created by Hermann on 04.09.2017.
 */
public class MotorController implements ArduinoResponseListener{
    private MotorControlPanelForm motorControlPanelForm;
    private CommandSenderInterface commandSenderFront;
    private CommandSenderInterface commandSenderBack;

    public MotorController(MotorControlPanelForm panelForm, CommandSenderInterface commandSenderFront,
                           CommandSenderInterface commandSenderBack){
        this.motorControlPanelForm = panelForm;
        this.commandSenderFront = commandSenderFront;
        this.commandSenderBack = commandSenderBack;

        this.motorControlPanelForm.button_LL_front_corner.addActionListener(e -> stepButtonClicked(Motor.VC, -Constants.BIG_STEP));
        this.motorControlPanelForm.button_L_front_corner.addActionListener(e -> stepButtonClicked(Motor.VC, -Constants.SMALL_STEP));
        this.motorControlPanelForm.button_RR_front_corner.addActionListener(e -> stepButtonClicked(Motor.VC, Constants.BIG_STEP));
        this.motorControlPanelForm.button_R_front_corner.addActionListener(e -> stepButtonClicked(Motor.VC, Constants.SMALL_STEP));

        this.motorControlPanelForm.button_LL_front_roof.addActionListener(e -> stepButtonClicked(Motor.VR, Constants.BIG_STEP));
        this.motorControlPanelForm.button_L_front_roof.addActionListener(e -> stepButtonClicked(Motor.VR, Constants.SMALL_STEP));
        this.motorControlPanelForm.button_RR_front_roof.addActionListener(e -> stepButtonClicked(Motor.VR, -Constants.BIG_STEP));
        this.motorControlPanelForm.button_R_front_roof.addActionListener(e -> stepButtonClicked(Motor.VR, -Constants.SMALL_STEP));

        this.motorControlPanelForm.button_LL_back_corner.addActionListener(e -> stepButtonClicked(Motor.HC, -Constants.BIG_STEP));
        this.motorControlPanelForm.button_L_back_corner.addActionListener(e -> stepButtonClicked(Motor.HC, -Constants.SMALL_STEP));
        this.motorControlPanelForm.button_RR_back_corner.addActionListener(e -> stepButtonClicked(Motor.HC, Constants.BIG_STEP));
        this.motorControlPanelForm.button_R_back_corner.addActionListener(e -> stepButtonClicked(Motor.HC, Constants.SMALL_STEP));

        this.motorControlPanelForm.button_LL_back_roof.addActionListener(e -> stepButtonClicked(Motor.HR, Constants.BIG_STEP));
        this.motorControlPanelForm.button_L_back_roof.addActionListener(e -> stepButtonClicked(Motor.HR, Constants.SMALL_STEP));
        this.motorControlPanelForm.button_RR_back_roof.addActionListener(e -> stepButtonClicked(Motor.HR, -Constants.BIG_STEP));
        this.motorControlPanelForm.button_R_back_roof.addActionListener(e -> stepButtonClicked(Motor.HR, -Constants.SMALL_STEP));
    }

    private void stepButtonClicked(Motor motor, int stepSize){
        try {
            switch (motor) {
                case VC:
                case VR:
                    commandSenderFront.sendCommand(motor.getMotorNumber() + Constants.STEP_CMD + stepSize);
                    break;
                case HC:
                case HR:
                    commandSenderBack.sendCommand(motor.getMotorNumber() + Constants.STEP_CMD + stepSize);
                    break;
            }
        } catch (Exception e){
            System.out.println("WARNING: Command has not been sent!");
            e.printStackTrace();
        }
    }

    // TODO update labels (and progress bars)
    public void arduinoResponse(Motor motor, String command, int value){
        switch(command){
            case Constants.POS_RESPONSE:
                switch(motor){
                    case VC:
                        this.motorControlPanelForm.posLabel_VC.setText("" + value);
                        break;
                    case VR:
                        this.motorControlPanelForm.posLabel_VR.setText("" + value);
                        break;
                    case HC:
                        this.motorControlPanelForm.posLabel_HC.setText("" + value);
                        break;
                    case HR:
                        this.motorControlPanelForm.posLabel_HR.setText("" + value);
                        break;
                }
                break;
            default:
        }
    }
}
