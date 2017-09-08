package main.ObserversAndListeners;

/**
 * Created by Hermann on 06.09.2017.
 */
public interface ArduinoConnectionListener {
    void connectionStatusUpdated(boolean isConnected, String arduinoSource);
}
