package main;

import com.sun.org.apache.xpath.internal.operations.Bool;
import main.ArduinoConnectedPanel.ConnectionPanelController;
import main.MotorControlPanel.MotorController;
import main.ObserversAndListeners.ArduinoConnectionListener;
import main.ObserversAndListeners.ArduinoConnectionObserver;
import main.ObserversAndListeners.ArduinoResponseListener;
import main.SerialConnection.*;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.TooManyListenersException;

/**
 * Created by Hermann on 04.09.2017.
 */
public abstract class CompressorController implements Observer {
    private static SerialConnectionHandler connectionFront = new SerialConnectionHandler();
    private static SerialConnectionHandler connectionBack = new SerialConnectionHandler();

    private static SerialPort serialPortFront;
    private static SerialPort serialPortBack;
    private static InputStream inputStreamFront;
    private static OutputStream outputStreamFront;
    private static InputStream inputStreamBack;
    private static OutputStream outputStreamBack;
    private static CommandSenderInterface commandSenderFront;
    private static CommandSenderInterface commandSenderBack;

    public static boolean frontIsConnected;
    public static boolean backIsConnected;

    private static MainWindow mainWindow;

    private static MotorController motorController;
    private static ConnectionPanelController connectionPanelController;

    public static void main(String[] args){
        initializeController();

    }

    private static void initializeController(){
        mainWindow = new MainWindow();

        initializeSerialConnections();
        initializeArduinoSendersAndListeners();
        initializeControlPanel();
        initializeConnectionPanel();
        initializeObservers();
    }

    private static void initializeControlPanel(){
        motorController = new MotorController(mainWindow.motorControlPanelForm, commandSenderFront, commandSenderBack);
    }

    private static void initializeConnectionPanel(){
        connectionPanelController = new ConnectionPanelController(mainWindow.connectedPanelForm);
    }

    private static void initializeArduinoSendersAndListeners(){
        ArduinoResponseListener arduinoResponseListeners[] = {motorController};
        try {

            if(frontIsConnected){
                serialPortFront.addEventListener(new ArduinoListener(inputStreamFront, "front", arduinoResponseListeners));
                serialPortFront.notifyOnDataAvailable(true);
                System.out.println("The controller is now listening to the front Arduinos responses.");
            }
            if(backIsConnected) {
                serialPortBack.addEventListener(new ArduinoListener(inputStreamBack, "back", arduinoResponseListeners));
                serialPortBack.notifyOnDataAvailable(true);
                System.out.println("The controller is now listening to the back Arduinos responses.");
            }

        } catch (Exception e){
            //NullPointerException is caught in case of no connection
            //but the connection flags in CompressorController are set false by the ConnectionHandlers
            //e.printStackTrace();
            System.out.println("WARNING: The controller is not listening to all Arduinos.");
        }

        if (outputStreamFront != null && inputStreamFront != null) {
            commandSenderFront = new CommandSender(outputStreamFront, inputStreamFront);
            System.out.println("The command sender (front) has been initialized.");
        } else {
            commandSenderFront = new CommandSenderDummy();
            System.out.println("No command sender (front) has been initialized.");
        }

        if(outputStreamBack != null && inputStreamBack != null){
            commandSenderBack = new CommandSender(outputStreamBack,inputStreamBack);
            System.out.println("The command sender (back) has been initialized.");
        } else {
            commandSenderBack = new CommandSenderDummy();
            System.out.println("No command sender (back) has been initialized.");
        }
    }

    private static void initializeSerialConnections(){
        //connectionFront.addObserver(this);
        //connectionBack.addObserver(this);
        try {
            serialPortFront = connectionFront.initializeConnection(Constants.portFront);
            inputStreamFront = connectionFront.getInputStream();
            outputStreamFront = connectionFront.getOutputStream();
            frontIsConnected = true;
        } catch (Exception e){
            // If there is no connection, a NullPointrException is caught
            //e.printStackTrace();
            frontIsConnected = false;
            // TODO dummyController in case of failed connection
        }

        try {
            serialPortBack = connectionBack.initializeConnection(Constants.portBack);
            inputStreamBack = connectionBack.getInputStream();
            outputStreamBack = connectionBack.getOutputStream();
            backIsConnected = true;
        } catch (Exception e){
            // If there is no connection, a NullPointrException is caught
            //e.printStackTrace();
            backIsConnected = false;
            // TODO dummyController in case of failed connection
        }
    }

    private static void initializeObservers(){
        // In case of reconnection all old observers are deleted, so there's no risk of "double observation"
        // if that is even a thing
        connectionFront.deleteObservers();
        connectionBack.deleteObservers();

        ArduinoConnectionObserver arduinoConnectionObserver = new ArduinoConnectionObserver(
                new ArduinoConnectionListener[] {connectionPanelController});
        connectionFront.addObserver(arduinoConnectionObserver);
        connectionBack.addObserver(arduinoConnectionObserver);
    }

    public static SerialPort getSerialPortFront(){
        return serialPortFront;
    }

    public static SerialPort getSerialPortBack(){
        return serialPortBack;
    }

    public void update(Observable obs, Object obj){
        if(obj instanceof Boolean) {
            if (obs == connectionFront) {
                this.frontIsConnected = (Boolean) obj;
            } else {
                if (obs == connectionBack) {
                    this.backIsConnected = (Boolean) obj;
                }
            }
        }
    }

    public static void disconnect() {
        try {
            connectionFront.closeConnection();
        } catch (NullPointerException e) {
            System.out.println("WARNING: System tried to disconnect from (front) Arduino, but there was no connection to close.");
        }

        try {
            connectionBack.closeConnection();
        }catch (NullPointerException e){
            System.out.println("WARNING: System tried to disconnect from (back) Arduino, but there was no connection to close.");
        }
    }

    public static void reConnect(){
        initializeSerialConnections();
        initializeArduinoSendersAndListeners();
        initializeObservers();
    }
}
