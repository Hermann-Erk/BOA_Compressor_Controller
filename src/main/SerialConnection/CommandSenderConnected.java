package main.SerialConnection;

/**
 * Created by Hermann on 14.09.2017.
 */

/**
 * This should be implemented by all control panels, that use the command senders to send data to the arduino.
 * It forces the controllers to implement a method to update the commandSenders if the connection is lost and
 * then re-established. In this case new command Senders are initialized and the controllers might still refer
 * to the old ones.
 */
public interface CommandSenderConnected {
    void setNewCommandSenders(CommandSenderInterface frontSender, CommandSenderInterface backSender);
}
