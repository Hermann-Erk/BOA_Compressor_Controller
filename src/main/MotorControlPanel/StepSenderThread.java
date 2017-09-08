package main.MotorControlPanel;

import main.Constants;
import main.Motor;
import main.SerialConnection.CommandSender;
import main.SerialConnection.CommandSenderInterface;

import java.util.TimerTask;



/**
 * Created by Hermann on 08.09.2017.
 */
public class StepSenderThread extends TimerTask {
    private CommandSenderInterface commandSenderFront;
    private CommandSenderInterface commandSenderBack;
    private Motor motor;
    private int stepSize;

    public StepSenderThread(CommandSenderInterface commandSenderFront, CommandSenderInterface commandSenderBack,
                            Motor motor, int stepSize){
    this.commandSenderFront = commandSenderFront;
    this.commandSenderBack = commandSenderBack;
    this.motor = motor;
    this.stepSize = stepSize;
    }

    @Override
    public void run() {
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
            } catch (Exception e) {
                System.out.println("WARNING: Command has not been sent!");
                e.printStackTrace();
            }
    }
}
