package main.SerialConnection;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import main.Constants;
import main.Motor;
import main.ObserversAndListeners.ArduinoResponseListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by Hermann on 04.09.2017.
 */
public class ArduinoListener implements SerialPortEventListener {
    private BufferedReader bufferedReader;
    private InputStream inputStream;
    private String arduino;

    private ArduinoResponseListener[] responseListeners;

    public ArduinoListener(InputStream input, String arduino, ArduinoResponseListener[] listeners)
            throws NullPointerException{
        this.inputStream = input;
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        this.arduino = arduino;
        this.responseListeners = listeners;
    }

    public void serialEvent(SerialPortEvent event){
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE){
            try{
                String inputLine = bufferedReader.readLine();
                System.out.println("Arduino to Java: " + inputLine);

                Scanner scanner = new Scanner(inputLine).useDelimiter(Constants.commandDelimiter);
                String motorString = scanner.next();
                String command = scanner.next();
                int number = scanner.nextInt();

                //System.out.println(motorString);
                //System.out.println(command);
                //System.out.println(number);

                Motor motor = Motor.getAbbreviation(arduino, motorString);

                for (ArduinoResponseListener listener: responseListeners){
                    listener.arduinoResponse(motor, command, number);
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }



}
