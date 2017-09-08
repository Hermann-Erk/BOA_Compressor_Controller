package main.ObserversAndListeners;

import main.Motor;

/**
 * Created by Hermann on 05.09.2017.
 */
public interface ArduinoResponseListener {
    void arduinoResponse(Motor motor, String command, int value);
}
