package main;

import main.ArduinoConnectedPanel.ConnectedPanelForm;
import main.CalibrationControlPanel.CalibrationPanelForm;
import main.CommandInterfacePanel.CommandInterfacePanelForm;
import main.MotorControlPanel.MotorControlPanelForm;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Hermann on 04.09.2017.
 */
public class MainWindow extends JFrame {
    JPanel rootPanel;
    MotorControlPanelForm motorControlPanelForm;
    ConnectedPanelForm connectedPanelForm;
    CommandInterfacePanelForm commandSenderInterfaceForm;
    CalibrationPanelForm calibrationPanelForm;

    MainWindow() {
        super();

        setContentPane(rootPanel);
        this.setTitle("BOA Prism Compressor Controller");
        this.setVisible(true);
        pack();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Should disconnect both arduinos when closing the window and then close the program
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CompressorController.disconnect();
                super.windowClosing(e);
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
