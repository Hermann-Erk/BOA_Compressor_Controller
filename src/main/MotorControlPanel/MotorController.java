package main.MotorControlPanel;

import main.Constants;
import main.Motor;
import main.ObserversAndListeners.ArduinoResponseListener;
import main.SerialConnection.CommandSenderConnected;
import main.SerialConnection.CommandSenderInterface;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

/**
 * Created by Hermann on 04.09.2017.
 */
public class MotorController implements ArduinoResponseListener, CommandSenderConnected, Observer {
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

        this.motorControlPanelForm.button_LL_front_corner.getModel().addChangeListener(new ButtonModelListener(Motor.VC, "-B", this));
        this.motorControlPanelForm.button_L_front_corner.getModel().addChangeListener(new ButtonModelListener(Motor.VC, "-S", this));
        this.motorControlPanelForm.button_RR_front_corner.getModel().addChangeListener(new ButtonModelListener(Motor.VC, "B", this));
        this.motorControlPanelForm.button_R_front_corner.getModel().addChangeListener(new ButtonModelListener(Motor.VC, "S", this));
        this.motorControlPanelForm.progressBar_front_corner.setForeground(Constants.NOT_MOVING_PROGRESS_BAR_COLOR);
        //this.motorControlPanelForm.progressBar_front_corner.setValue(50);

        this.motorControlPanelForm.button_LL_front_roof.getModel().addChangeListener(new ButtonModelListener(Motor.VR, "-B", this));
        this.motorControlPanelForm.button_L_front_roof.getModel().addChangeListener(new ButtonModelListener(Motor.VR, "-S", this));
        this.motorControlPanelForm.button_RR_front_roof.getModel().addChangeListener(new ButtonModelListener(Motor.VR, "B", this));
        this.motorControlPanelForm.button_R_front_roof.getModel().addChangeListener(new ButtonModelListener(Motor.VR, "S", this));
        this.motorControlPanelForm.progressBar_front_roof.setForeground(Constants.NOT_MOVING_PROGRESS_BAR_COLOR);

        this.motorControlPanelForm.button_LL_back_corner.getModel().addChangeListener(new ButtonModelListener(Motor.HC, "-B", this));
        this.motorControlPanelForm.button_L_back_corner.getModel().addChangeListener(new ButtonModelListener(Motor.HC, "-S", this));
        this.motorControlPanelForm.button_RR_back_corner.getModel().addChangeListener(new ButtonModelListener(Motor.HC, "B", this));
        this.motorControlPanelForm.button_R_back_corner.getModel().addChangeListener(new ButtonModelListener(Motor.HC, "S", this));
        this.motorControlPanelForm.progressBar_back_corner.setForeground(Constants.NOT_MOVING_PROGRESS_BAR_COLOR);

        this.motorControlPanelForm.button_LL_back_roof.getModel().addChangeListener(new ButtonModelListener(Motor.HR, "-B", this));
        this.motorControlPanelForm.button_L_back_roof.getModel().addChangeListener(new ButtonModelListener(Motor.HR, "-S", this));
        this.motorControlPanelForm.button_RR_back_roof.getModel().addChangeListener(new ButtonModelListener(Motor.HR, "B", this));
        this.motorControlPanelForm.button_R_back_roof.getModel().addChangeListener(new ButtonModelListener(Motor.HR, "S", this));
        this.motorControlPanelForm.progressBar_back_roof.setForeground(Constants.NOT_MOVING_PROGRESS_BAR_COLOR);

        this.motorControlPanelForm.smallStepSlider.addChangeListener(e -> setSmallStep(e));
        this.motorControlPanelForm.bigStepSlider.addChangeListener(e -> setBigStep(e));
    }

    void stepButtonStateChanged(ChangeEvent e, Motor motor, int stepSize){
        if(e.getSource() instanceof ButtonModel){
            if(((ButtonModel) e.getSource()).isPressed()){
                stepSenderThread = new StepSenderThread(commandSenderFront, commandSenderBack, motor, stepSize);
                senderThreadTimer.scheduleAtFixedRate(stepSenderThread, 0, 100);
            } else {
                stepSenderThread.cancel();
            }
        }
    }

    public synchronized void arduinoResponse(Motor motor, String command, int value){
        switch(command){
            case Constants.POS_RESPONSE:
                //System.out.println("testest");
                switch(motor){
                    case VC:
                        this.motorControlPanelForm.posLabel_VC.setText("" + value);
                        this.motorControlPanelForm.progressBar_front_corner.setValue(value);
                        break;
                    case VR:
                        this.motorControlPanelForm.posLabel_VR.setText("" + value);
                        this.motorControlPanelForm.progressBar_front_roof.setValue(value);
                        break;
                    case HC:
                        this.motorControlPanelForm.posLabel_HC.setText("" + value);
                        this.motorControlPanelForm.progressBar_back_corner.setValue(value);
                        break;
                    case HR:
                        this.motorControlPanelForm.posLabel_HR.setText("" + value);
                        this.motorControlPanelForm.progressBar_back_roof.setValue(value);
                        break;
                }
                break;
            case Constants.MAX_LIMIT_RESPONSE:
                //System.out.println(motor);
                switch(motor){
                    case VC:
                        this.motorControlPanelForm.progressBar_front_corner.setMaximum(value);
                        this.motorControlPanelForm.maxLabel_front_corner.setText("Max" + value + "");
                        break;
                    case VR:
                        this.motorControlPanelForm.progressBar_front_roof.setMaximum(value);
                        this.motorControlPanelForm.maxLabel_front_roof.setText("Max" + value + "");
                        break;
                    case HC:
                        this.motorControlPanelForm.progressBar_back_corner.setMaximum(value);
                        this.motorControlPanelForm.maxLabel_back_corner.setText("Max" + value + "");
                        break;
                    case HR:
                        this.motorControlPanelForm.progressBar_back_roof.setMaximum(value);
                        this.motorControlPanelForm.maxLabel_back_roof.setText("Max" + value + "");
                        break;
                }
                break;
            case Constants.ARDUINO_STARTED_MOVING_RESPONSE:
                switch(motor){
                    case VC:
                        this.motorControlPanelForm.button_LL_front_corner.setEnabled(false);
                        this.motorControlPanelForm.button_L_front_corner.setEnabled(false);
                        this.motorControlPanelForm.button_RR_front_corner.setEnabled(false);
                        this.motorControlPanelForm.button_R_front_corner.setEnabled(false);
                        this.motorControlPanelForm.progressBar_front_corner.setForeground(Constants.MOVING_PROGRESS_BAR_COLOR);
                        break;
                    case VR:
                        this.motorControlPanelForm.button_LL_front_roof.setEnabled(false);
                        this.motorControlPanelForm.button_L_front_roof.setEnabled(false);
                        this.motorControlPanelForm.button_RR_front_roof.setEnabled(false);
                        this.motorControlPanelForm.button_R_front_roof.setEnabled(false);
                        this.motorControlPanelForm.progressBar_front_roof.setForeground(Constants.MOVING_PROGRESS_BAR_COLOR);
                        break;
                    case HC:
                        this.motorControlPanelForm.button_LL_back_corner.setEnabled(false);
                        this.motorControlPanelForm.button_L_back_corner.setEnabled(false);
                        this.motorControlPanelForm.button_RR_back_corner.setEnabled(false);
                        this.motorControlPanelForm.button_R_back_corner.setEnabled(false);
                        this.motorControlPanelForm.progressBar_back_corner.setForeground(Constants.MOVING_PROGRESS_BAR_COLOR);
                        break;
                    case HR:
                        this.motorControlPanelForm.button_LL_back_roof.setEnabled(false);
                        this.motorControlPanelForm.button_L_back_roof.setEnabled(false);
                        this.motorControlPanelForm.button_RR_back_roof.setEnabled(false);
                        this.motorControlPanelForm.button_R_back_roof.setEnabled(false);
                        this.motorControlPanelForm.progressBar_back_roof.setForeground(Constants.MOVING_PROGRESS_BAR_COLOR);
                        break;
                }
                break;
            case Constants.ARDUINO_STOPPED_MOVING_RESPONSE:
                switch(motor){
                    case VC:
                        this.motorControlPanelForm.button_LL_front_corner.setEnabled(true);
                        this.motorControlPanelForm.button_L_front_corner.setEnabled(true);
                        this.motorControlPanelForm.button_RR_front_corner.setEnabled(true);
                        this.motorControlPanelForm.button_R_front_corner.setEnabled(true);
                        this.motorControlPanelForm.progressBar_front_corner.setForeground(Constants.NOT_MOVING_PROGRESS_BAR_COLOR);
                        break;
                    case VR:
                        this.motorControlPanelForm.button_LL_front_roof.setEnabled(true);
                        this.motorControlPanelForm.button_L_front_roof.setEnabled(true);
                        this.motorControlPanelForm.button_RR_front_roof.setEnabled(true);
                        this.motorControlPanelForm.button_R_front_roof.setEnabled(true);
                        this.motorControlPanelForm.progressBar_front_roof.setForeground(Constants.NOT_MOVING_PROGRESS_BAR_COLOR);
                        break;
                    case HC:
                        this.motorControlPanelForm.button_LL_back_corner.setEnabled(true);
                        this.motorControlPanelForm.button_L_back_corner.setEnabled(true);
                        this.motorControlPanelForm.button_RR_back_corner.setEnabled(true);
                        this.motorControlPanelForm.button_R_back_corner.setEnabled(true);
                        this.motorControlPanelForm.progressBar_back_corner.setForeground(Constants.NOT_MOVING_PROGRESS_BAR_COLOR);
                        break;
                    case HR:
                        this.motorControlPanelForm.button_LL_back_roof.setEnabled(true);
                        this.motorControlPanelForm.button_L_back_roof.setEnabled(true);
                        this.motorControlPanelForm.button_RR_back_roof.setEnabled(true);
                        this.motorControlPanelForm.button_R_back_roof.setEnabled(true);
                        this.motorControlPanelForm.progressBar_back_roof.setForeground(Constants.NOT_MOVING_PROGRESS_BAR_COLOR);
                        break;
                }
                break;
            case Constants.ARDUINO_COULD_NOT_LOAD_FROM_EEPROM_RESPONSE:
                switch(motor){
                    case VC:
                    case VR:
                        this.motorControlPanelForm.progressBar_front_corner.setForeground(Constants.COULDNT_LOAD_FROM_EEPROM_COLOR);
                        this.motorControlPanelForm.progressBar_front_roof.setForeground(Constants.COULDNT_LOAD_FROM_EEPROM_COLOR);
                        this.motorControlPanelForm.refLabel_front_corner.setText("Ref. to zero!");
                        this.motorControlPanelForm.refLabel_front_roof.setText("Ref. to zero!");
                        break;
                    case HC:
                    case HR:
                        this.motorControlPanelForm.progressBar_back_corner.setForeground(Constants.COULDNT_LOAD_FROM_EEPROM_COLOR);
                        this.motorControlPanelForm.progressBar_back_roof.setForeground(Constants.COULDNT_LOAD_FROM_EEPROM_COLOR);
                        this.motorControlPanelForm.refLabel_back_corner.setText("Ref. to zero!");
                        this.motorControlPanelForm.refLabel_back_roof.setText("Ref. to zero!");
                        break;
                }
            default:
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Motor motor = (Motor) arg;
        switch(motor){
            case VC:
                this.motorControlPanelForm.refLabel_front_corner.setText("");
                break;
            case VR:
                this.motorControlPanelForm.refLabel_front_roof.setText("");
                break;
            case HC:
                this.motorControlPanelForm.refLabel_back_corner.setText("");
                break;
            case HR:
                this.motorControlPanelForm.refLabel_back_roof.setText("");
                break;
        }
    }

    private void setSmallStep(ChangeEvent e){
        JSlider smallStepSlider = (JSlider) e.getSource();
        Constants.SMALL_STEP = smallStepSlider.getValue();
    }

    private void setBigStep(ChangeEvent e){
        JSlider bigStepSlider = (JSlider) e.getSource();
        Constants.BIG_STEP = bigStepSlider.getValue();
    }

    // Needed when refreshing the connection, otherwise the commands are sent into an empty output stream
    public void setNewCommandSenders(CommandSenderInterface frontSender, CommandSenderInterface backSender){
        this.commandSenderFront = frontSender;
        this.commandSenderBack = backSender;
    }
}
