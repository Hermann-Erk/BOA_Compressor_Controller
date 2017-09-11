package main.ArduinoConnectedPanel;

import main.CompressorController;
import main.ObserversAndListeners.ArduinoConnectionListener;

import java.awt.*;

/**
 * Created by Hermann on 06.09.2017.
 */
public class ConnectionPanelController implements ArduinoConnectionListener{
    private ConnectedPanelForm connectionPanel;

    public ConnectionPanelController(ConnectedPanelForm panel){
        this.connectionPanel = panel;
        this.connectionStatusUpdated(CompressorController.frontIsConnected,"front");
        this.connectionStatusUpdated(CompressorController.backIsConnected,"back");

        this.connectionPanel.connectButton.addActionListener(e -> reConnect());
        this.connectionPanel.disconnectButton.addActionListener(e -> disconnect());
    }

    public void connectionStatusUpdated(boolean isConnected, String arduinoSource){
        //System.err.println(isConnected);
        //System.err.println(arduinoSource);
        switch (arduinoSource){
            case "front":
                if (isConnected){
                    connectionPanel.arduinoFrontConnectionLabel.setForeground(Color.GREEN);
                    connectionPanel.arduinoFrontConnectionLabel.setText("Connected");
                } else {
                    connectionPanel.arduinoFrontConnectionLabel.setForeground(Color.RED);
                    connectionPanel.arduinoFrontConnectionLabel.setText("Disconnected");
                }
                break;
            case "back":
                if (isConnected){
                    connectionPanel.arduinoBackConnectionLabel.setForeground(Color.GREEN);
                    connectionPanel.arduinoBackConnectionLabel.setText("Connected");
                } else {
                    connectionPanel.arduinoBackConnectionLabel.setForeground(Color.RED);
                    connectionPanel.arduinoBackConnectionLabel.setText("Disconnected");
                }
                break;
        }
    }

    private void disconnect(){
        CompressorController.disconnect();
    }

    private void reConnect(){
        CompressorController.reConnect();
    }
}
