package main.SerialConnection;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import main.CompressorController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;

/**
 * Created by Hermann on 04.09.2017.
 */
public class SerialConnectionHandler extends Observable{
    private SerialPort serialPort = null;
    private InputStream inputStream;
    private OutputStream outputStream;

    public SerialPort initializeConnection(String comPort) throws Exception{
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(comPort);

            CommPort commPort = portIdentifier.open(this.getClass().getName(), 5000);

            this.serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            System.out.println("Connection established with port " + comPort + ".");

            setChanged();
            notifyObservers(serialPort);

        } catch (NoSuchPortException e){
            System.out.println("WARNING: Port " + comPort + " not found.");
            setChanged();
            notifyObservers(null);
        }
        return serialPort;
    }

    public void closeConnection() throws NullPointerException{
        setChanged();
        notifyObservers(null);
        serialPort.close();
        System.out.println("Connection with port " + serialPort.getName() + " was closed.");
        this.serialPort = null;
    }

    public InputStream getInputStream() throws IOException {
        return this.serialPort.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.serialPort.getOutputStream();
    }

}
