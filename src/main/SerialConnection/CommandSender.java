package main.SerialConnection;

import main.Constants;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Hermann on 04.09.2017.
 */
public class CommandSender implements  CommandSenderInterface{
    private OutputStream outputStream;
    private InputStream inputStream; //probably not used

    public CommandSender(OutputStream output, InputStream input){
        this.outputStream = output;
        this.inputStream = input;
    }

    public synchronized void sendCommand(String commandString){
        // TODO test for functionality... this was done to separate different commands
        try {
            String commandStringPlusLineBreak = commandString + "\n";
            outputStream.write(commandStringPlusLineBreak.getBytes());
            System.out.println("Java to Arduino: " + commandString);
        }catch (Exception e){
            System.out.println("WARNING: Command " + commandString +
                    " has not been sent!");
        }
    }

}
