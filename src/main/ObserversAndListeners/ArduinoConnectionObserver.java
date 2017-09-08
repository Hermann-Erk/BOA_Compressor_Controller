package main.ObserversAndListeners;

import gnu.io.CommPort;
import gnu.io.SerialPort;
import main.CompressorController;
import main.Constants;
import main.SerialConnection.SerialConnectionHandler;

import javax.sql.ConnectionEventListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Hermann on 06.09.2017.
 */
public class ArduinoConnectionObserver implements Observer{
    private ArduinoConnectionListener[] connectionListeners;

    public ArduinoConnectionObserver(ArduinoConnectionListener[] listeners){
        this.connectionListeners = listeners;
    }

    public void update(Observable o, Object serialPort){
        String arduinoSource = "";
        if(o instanceof SerialConnectionHandler) {
            if (o == CompressorController.getConnectionHandlerFront()) {
                arduinoSource = "front";
            } else {
                if (o == CompressorController.getConnectionHandlerBack()) {
                    arduinoSource = "back";
                }
            }
        }
        for (ArduinoConnectionListener listener: connectionListeners){
            if (serialPort instanceof SerialPort) {
                listener.connectionStatusUpdated(true, arduinoSource);
            }else {
                listener.connectionStatusUpdated(false, arduinoSource);
            }
        }
    }
}
