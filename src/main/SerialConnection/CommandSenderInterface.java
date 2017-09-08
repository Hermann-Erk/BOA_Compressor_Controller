package main.SerialConnection;

/**
 * Created by Hermann on 06.09.2017.
 */
public interface CommandSenderInterface {
    public void sendCommand(String commandString) throws Exception;
}
