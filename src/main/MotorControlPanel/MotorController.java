package main.MotorControlPanel;

import main.Constants;
import main.Motor;
import main.ObserversAndListeners.ArduinoResponseListener;
import main.SerialConnection.CommandSenderInterface;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.Timer;

/**
 * Created by Hermann on 04.09.2017.
 */
public class MotorController implements ArduinoResponseListener{
    private MotorControlPanelForm motorControlPanelForm;
    private CommandSenderInterface commandSenderFront;
    private CommandSenderInterface commandSenderBack;
    private static StepSenderThread stepSenderThread;
    public static Timer senderThreadTimer = new Timer();

    public MotorController(MotorControlPanelForm panelForm, CommandSenderInterface commandSenderFront,
                           CommandSenderInterface commandSenderBack){
        this.motorControlPanelForm = panelForm;
        this.commandSenderFront = commandSenderFront;
        this.commandSenderBack = commandSenderBack;

        this.motorControlPanelForm.button_LL_front_corner.getModel().addChangeListener(new ButtonModelListener(Motor.VC, -Constants.BIG_STEP, this));
        this.motorControlPanelForm.button_L_front_corner.getModel().addChangeListener(new ButtonModelListener(Motor.VC, -Constants.SMALL_STEP, this));
        this.motorControlPanelForm.button_RR_front_corner.getModel().addChangeListener(new ButtonModelListener(Motor.VC, Constants.BIG_STEP, this));
        this.motorControlPanelForm.button_R_front_corner.getModel().addChangeListener(new ButtonModelListener(Motor.VC, Constants.SMALL_STEP, this));

        this.motorControlPanelForm.button_LL_front_roof.getModel().addChangeListener(new ButtonModelListener(Motor.VR, Constants.BIG_STEP, this));
        this.motorControlPanelForm.button_L_front_roof.getModel().addChangeListener(new ButtonModelListener(Motor.VR, Constants.SMALL_STEP, this));
        this.motorControlPanelForm.button_RR_front_roof.getModel().addChangeListener(new ButtonModelListener(Motor.VR, -Constants.BIG_STEP, this));
        this.motorControlPanelForm.button_R_front_roof.getModel().addChangeListener(new ButtonModelListener(Motor.VR, -Constants.SMALL_STEP, this));

        this.motorControlPanelForm.button_LL_back_corner.getModel().addChangeListener(new ButtonModelListener(Motor.HC, -Constants.BIG_STEP, this));
        this.motorControlPanelForm.button_L_back_corner.getModel().addChangeListener(new ButtonModelListener(Motor.HC, -Constants.SMALL_STEP, this));
        this.motorControlPanelForm.button_RR_back_corner.getModel().addChangeListener(new ButtonModelListener(Motor.HC, Constants.BIG_STEP, this));
        this.motorControlPanelForm.button_R_back_corner.getModel().addChangeListener(new ButtonModelListener(Motor.HC, Constants.SMALL_STEP, this));

        this.motorControlPanelForm.button_LL_back_roof.getModel().addChangeListener(new ButtonModelListener(Motor.HR, Constants.BIG_STEP, this));
        this.motorControlPanelForm.button_L_back_roof.getModel().addChangeListener(new ButtonModelListener(Motor.HR, Constants.SMALL_STEP, this));
        this.motorControlPanelForm.button_RR_back_roof.getModel().addChangeListener(new ButtonModelListener(Motor.HR, -Constants.BIG_STEP, this));
        this.motorControlPanelForm.button_R_back_roof.getModel().addChangeListener(new ButtonModelListener(Motor.HR, -Constants.SMALL_STEP, this));
    }

    void stepButtonStateChanged(ChangeEvent e, Motor motor, int stepSize){
        if(e.getSource() instanceof ButtonModel){
            if(((ButtonModel) e.getSource()).isPressed()){
                stepSenderThread = new StepSenderThread(commandSenderFront, commandSenderBack, motor, stepSize);
                //stepSenderThread.run();
                senderThreadTimer.scheduleAtFixedRate(stepSenderThread, 0, 100);
            } else {
                System.err.println("testtestset");
                stepSenderThread.cancel();
            }
        }
        /*
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
        }*/
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

    // Needed when refreshing the connection, otherwise the commands are sent into an empty output stream
    public void setNewCommandSenders(CommandSenderInterface frontSender, CommandSenderInterface backSender){
        this.commandSenderFront = frontSender;
        this.commandSenderBack = backSender;
    }
}
