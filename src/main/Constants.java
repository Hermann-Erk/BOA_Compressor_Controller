package main;

/**
 * Created by Hermann on 04.09.2017.
 */
public class Constants {
    //public static final String portFront = "COM6";
    //public static final String portBack = "COM5";
    public static final String portFront = "COM6";
    public static final String portBack = "COM12";
    public static final String commandDelimiter = " ";

    public static int SMALL_STEP = 5;
    public static int BIG_STEP = 20;

    //public static final String STEP_CMD = "b";
    public static final String STEP_CMD = "m";
    public static final String ABS_MOVE_CMD = "a";
    public static final String REL_MOVE_CMD = "m";
    public static final String STOP_CMD = "x";
    public static final String REFERENCE_CMD = "r";
    public static final String MEASURE_STAGE_CMD = "z";

    public static final String POS_RESPONSE = "p";
    public static final String MAX_LIMIT_RESPONSE = "l";
}
