package main.SerialConnection;

/**
 * Created by Hermann on 06.09.2017.
 */
public class CommandSenderDummy implements CommandSenderInterface{

    @Override
    public void sendCommand(String commandString) {
        System.out.println("WARNING: Command " + commandString + " has not been sent by the dummy.");
    }

}
