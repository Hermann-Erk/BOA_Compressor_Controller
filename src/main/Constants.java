package main;

import java.awt.*;

/**
 * Created by Hermann on 04.09.2017.
 */
public class Constants {
    //public static final String portFront = "COM6";
    //public static final String portBack = "COM5";
    public static final String portFront = "COM6";
    public static final String portBack = "COM5";
    public static final String commandDelimiter = " ";

    public static final String calibrationFilePath2H = "C:\\Users\\Hermann\\Documents\\GitHub\\BOA_Compressor_Controller\\calibration\\calibrationFile2H.txt";
    public static final String calibrationFilePath3H = "C:\\Users\\Hermann\\Documents\\GitHub\\BOA_Compressor_Controller\\calibration\\calibrationFile3H.txt";


    public static int SMALL_STEP = 200;
    public static int BIG_STEP = 2000;

    //public static final String STEP_CMD = "b";
    public static final String STEP_CMD = "m";
    public static final String ABS_MOVE_CMD = "a";
    public static final String REL_MOVE_CMD = "m";
    public static final String STOP_CMD = "x";
    public static final String REFERENCE_CMD = "r";
    public static final String MEASURE_STAGE_CMD = "z";

    public static final String POS_RESPONSE = "p";
    public static final String MAX_LIMIT_RESPONSE = "l";
    public static final String ARDUINO_STARTED_MOVING_RESPONSE = "f";
    public static final String ARDUINO_STOPPED_MOVING_RESPONSE = "s";
    public static final String ARDUINO_COULD_NOT_LOAD_FROM_EEPROM_RESPONSE = "y";

    public static final Color MOVING_PROGRESS_BAR_COLOR = Color.ORANGE;
    public static final Color NOT_MOVING_PROGRESS_BAR_COLOR = Color.GREEN;
    public static final Color COULDNT_LOAD_FROM_EEPROM_COLOR = Color.RED;
}
