package main;

import main.ArduinoConnectedPanel.ConnectionPanelController;
import main.CalibrationControlPanel.CalibrationPanelController;
import main.CalibrationData.CalibrationFileParser;
import main.CommandInterfacePanel.CommandInterfacePanelController;
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

/**
 * Created by Hermann on 04.09.2017.
 */
public abstract class CompressorController implements Observer {
    private static SerialConnectionHandler connectionHandlerFront = new SerialConnectionHandler();
    private static SerialConnectionHandler connectionHandlerBack = new SerialConnectionHandler();

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
    private static CommandInterfacePanelController commandInterfacePanelController;
    private static CalibrationPanelController calibrationPanelController;

    public static void main(String[] args){
        initializeController();

    }

    private static void initializeController(){
        mainWindow = new MainWindow();

        /*
        CalibrationFileParser parser = new CalibrationFileParser();

        try {
            CalibrationFileParser.printCalibrationDataToConsole(parser.readCalibrationFile(Constants.calibrationFilePath));
        }catch(Exception e){
            e.printStackTrace();
        }
        */

        initializeSerialConnections();
        initializeArduinoSenders();
        initializeControlPanel();
        initializeConnectionPanel();
        initializeCommandInterfacePanel();
        initializeCalibrationPanel();
        initializeArduinoListeners();
        initializeObservers();
    }

    private static void initializeCalibrationPanel(){
        calibrationPanelController = new CalibrationPanelController(mainWindow.calibrationPanelForm,
                commandSenderFront, commandSenderBack);
    }

    private static void initializeCommandInterfacePanel(){
        commandInterfacePanelController = new CommandInterfacePanelController(mainWindow.commandSenderInterfaceForm,
                commandSenderFront, commandSenderBack);
    }

    private static void initializeControlPanel(){
        motorController = new MotorController(mainWindow.motorControlPanelForm, commandSenderFront, commandSenderBack);
    }

    private static void initializeConnectionPanel(){
        connectionPanelController = new ConnectionPanelController(mainWindow.connectedPanelForm);
    }

    private static void initializeArduinoListeners(){
        ArduinoResponseListener arduinoResponseListeners[]
            = {motorController, calibrationPanelController, commandInterfacePanelController};
        try {
            //System.out.println(motorController);
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
    }

    private static void initializeArduinoSenders(){
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
        //connectionHandlerFront.addObserver(this);
        //connectionHandlerBack.addObserver(this);
        try {
            serialPortFront = connectionHandlerFront.initializeConnection(Constants.portFront);
            inputStreamFront = connectionHandlerFront.getInputStream();
            outputStreamFront = connectionHandlerFront.getOutputStream();
            frontIsConnected = true;
        } catch (Exception e){
            // If there is no connection, a NullPointrException is caught
            //e.printStackTrace();
            frontIsConnected = false;
        }

        try {
            serialPortBack = connectionHandlerBack.initializeConnection(Constants.portBack);
            inputStreamBack = connectionHandlerBack.getInputStream();
            outputStreamBack = connectionHandlerBack.getOutputStream();
            backIsConnected = true;
        } catch (Exception e){
            // If there is no connection, a NullPointrException is caught
            //e.printStackTrace();
            backIsConnected = false;
        }
    }

    private static void initializeObservers(){
        // In case of reconnection all old observers are deleted, so there's no risk of "double observation"
        // if that is even a thing
        connectionHandlerFront.deleteObservers();
        connectionHandlerBack.deleteObservers();

        //System.out.println(connectionPanelController);
        ArduinoConnectionObserver arduinoConnectionObserver = new ArduinoConnectionObserver(
                new ArduinoConnectionListener[] {connectionPanelController});
        connectionHandlerFront.addObserver(arduinoConnectionObserver);
        connectionHandlerBack.addObserver(arduinoConnectionObserver);

        commandInterfacePanelController.addObserver(motorController);
    }

    public static SerialPort getSerialPortFront(){
        return serialPortFront;
    }

    public static SerialPort getSerialPortBack(){
        return serialPortBack;
    }

    public void update(Observable obs, Object obj){
        if(obj instanceof Boolean) {
            if (obs == connectionHandlerFront) {
                this.frontIsConnected = (Boolean) obj;
            } else {
                if (obs == connectionHandlerBack) {
                    this.backIsConnected = (Boolean) obj;
                }
            }
        }
    }

    public static void disconnect() {
        try {
            connectionHandlerFront.closeConnection();
        } catch (NullPointerException e) {
            System.out.println("WARNING: System tried to disconnect from (front) Arduino, but there was no connection to close.");
        }

        try {
            connectionHandlerBack.closeConnection();
        }catch (NullPointerException e){
            System.out.println("WARNING: System tried to disconnect from (back) Arduino, but there was no connection to close.");
        }
    }

    public static void reConnect(){
        disconnect();
        initializeSerialConnections();
        initializeArduinoSenders();
        initializeArduinoListeners();
        initializeObservers();
        motorController.setNewCommandSenders(commandSenderFront, commandSenderBack);
        calibrationPanelController.setNewCommandSenders(commandSenderFront, commandSenderBack);
        commandInterfacePanelController.setNewCommandSenders(commandSenderFront, commandSenderBack);
    }

    public static SerialConnectionHandler getConnectionHandlerFront() {
        return connectionHandlerFront;
    }

    public static SerialConnectionHandler getConnectionHandlerBack() {
        return connectionHandlerBack;
    }
}
