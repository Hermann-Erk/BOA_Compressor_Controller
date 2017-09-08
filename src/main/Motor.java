package main;

/**
 * Created by Hermann on 05.09.2017.
 */
public enum Motor {
    VC,
    VR,
    HC,
    HR;

    public String toString(){
        switch(this){
            case VC:
                return "front cornercube";
            case VR:
                return "front roofmirror";
            case HC:
                return "back cornercube";
            case HR:
                return "back roofmirror";
            default:
                return "";
        }
    }

    // TODO check if the motor numbers in the Arduino program correspond to the numbers below (1 and 2)
    public int getMotorNumber(){
        switch(this){
            case VC:
            case HC:
                return 1;
            case VR:
            case HR:
                return 2;
            default:
                return -1;
        }
    }

    public static Motor getAbbreviation(String arduino, String motor) {
        int motorNumber = Integer.parseInt(motor);
        if (arduino.equals("front")) {
            if (motorNumber == 1) {
                return VC;
            } else {
                if (motorNumber == 2) {
                    return VR;
                }
            }
        } else {
            if (arduino.equals("back")) {
                if (motorNumber == 1) {
                    return HC;
                } else {
                    if (motorNumber == 2) {
                        return HR;
                    }
                }
            }
        }
        return null;
    }
}
