package main.MotorControlPanel;

import main.Constants;
import main.Motor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by Hermann on 08.09.2017.
 */
public class ButtonModelListener implements ChangeListener {
    private boolean isCurrentlyPressed = false;
    private MotorController motorController;
    private Motor motor;
    private String step;

    public ButtonModelListener(Motor motor, String step, MotorController motorController){
        this.motorController = motorController;
        this.motor = motor;
        this.step = step;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ButtonModel buttonModel  = (ButtonModel) e.getSource();

        if(buttonModel.isPressed() != isCurrentlyPressed){
            switch(step){
                case "B":
                    motorController.stepButtonStateChanged(e, motor, Constants.BIG_STEP);
                    break;
                case "-B":
                    motorController.stepButtonStateChanged(e, motor, -Constants.BIG_STEP);
                    break;
                case "S":
                    motorController.stepButtonStateChanged(e, motor, Constants.SMALL_STEP);
                    break;
                case "-S":
                    motorController.stepButtonStateChanged(e, motor, -Constants.SMALL_STEP);
                    break;
            }
            isCurrentlyPressed = buttonModel.isPressed();
        }
    }
}
