package main.MotorControlPanel;

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
    private int step;

    public ButtonModelListener(Motor motor, int step, MotorController motorController){
        this.motorController = motorController;
        this.motor = motor;
        this.step = step;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        ButtonModel buttonModel  = (ButtonModel) e.getSource();

        if(buttonModel.isPressed() != isCurrentlyPressed){
            motorController.stepButtonStateChanged(e, motor, step);
            isCurrentlyPressed = buttonModel.isPressed();
        }
    }
}
