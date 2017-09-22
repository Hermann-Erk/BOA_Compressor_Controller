package main.CalibrationData;

import java.util.ArrayList;

/**
 * Created by Hermann on 14.09.2017.
 */
public class CalibrationCalculator {
    //2H is front, 3H is back
    private ArrayList<int[]> calibrationData_2H;
    private ArrayList<int[]> calibrationData_3H;

    public CalibrationCalculator(ArrayList<int[]> calibrationData_2H, ArrayList<int[]> getCalibrationData_3H){
        this.calibrationData_2H = calibrationData_2H;
        this.calibrationData_3H = getCalibrationData_3H;
    }

    /**
     * calculates the positions of all motors for the given wavelengths in steps
     * ATTENTION: If the set wavelength is a value between the second harmonic and the fundamental (which should never be
     * the case), the positions for the cornercubes can still be calculated
     *
     * @param wavelength_2H
     * @param wavelength_3H
     * @return position set for all motors to compensate the GVD (according to the calibration files)
     * and (when both NOPAs are used) to achieve a pulse overlap on the sample (in the UHV chamber) using
     * the roof mirrors.
     */
    public PositionSet calculatePosition (int wavelength_2H, int wavelength_3H) throws IndexOutOfBoundsException{
        // Checks, if the NOPAs use the fundamental beam or the second harmonic
        // (determined by the wavelength)
        boolean is2HinSecondHarmonicMode = wavelength_2H < 460;
        boolean is3HinSecondHarmonicMode = wavelength_3H < 480;

        // If one of the wavelengths is set -1, the respective motors for the NOPA are omitted
        boolean use2H_NOPA = wavelength_2H != -1;
        boolean use3H_NOPA = wavelength_3H != -1;

        int cornerCubePos2H = -1;
        int cornerCubePos3H = -1;
        int roofMirrorPos2H = -1;
        int roofMirrorPos3H = -1;

        if(use2H_NOPA) {
            cornerCubePos2H = calculateCornerCubePosition(wavelength_2H, 2);
        }else{
            // If the 2H Nopa is not in use, the roof mirror of the 3H Stage can be
            // set to zero to minimize the beam's path.
            // The roof mirror and corner cube position for the 2H NOPA are both -1, this will
            // signal the CalibrationPanelController to not send these respective commands.
            roofMirrorPos3H = 0;
        }

        if(use3H_NOPA) {
            cornerCubePos3H = calculateCornerCubePosition(wavelength_3H, 3);
        }else{
            roofMirrorPos2H = 0;
        }

        if(use2H_NOPA && use3H_NOPA){
            roofMirrorPos2H = 0;
            roofMirrorPos3H = 0;
            if(cornerCubePos2H > cornerCubePos3H){
                int deltaPos = cornerCubePos2H - cornerCubePos3H;
                roofMirrorPos2H = 2 * deltaPos;
            }else {
                int deltaPos = cornerCubePos3H - cornerCubePos2H;
                roofMirrorPos3H = 2 * deltaPos;
            }

            if(is2HinSecondHarmonicMode){
                //(193209 steps)/(96.5 cm) for the 3H NOPA i.e. 60065 steps for 30 cm
                roofMirrorPos3H = roofMirrorPos3H + 60065;
            }

            if(is3HinSecondHarmonicMode){
                //(193877 steps)/(96.9 cm) for the 2H NOPA i.e. 60024 steps for 30 cm
                roofMirrorPos2H = roofMirrorPos2H + 60024;
            }

            // Due to the longer beam path within the nopa during the use of the second harmonic
            // it is possible for both roof mirrors to be at a different position than zero.
            // However, it is practical for the beam path to be as short as possible (while keeping
            // both beam paths at the same length). This is why at least one mirror should be at
            // position zero, the other one can be moved accordingly. The following code should
            // guarantee the shortest beam path
            if(roofMirrorPos2H != 0 && roofMirrorPos3H != 0){
                if(roofMirrorPos2H > roofMirrorPos3H){
                    roofMirrorPos2H = roofMirrorPos2H - roofMirrorPos3H;
                    roofMirrorPos3H = 0;
                }else{
                    roofMirrorPos3H = roofMirrorPos3H - roofMirrorPos2H;
                    roofMirrorPos2H = 0;

                }
            }
        }


        PositionSet position = new PositionSet(cornerCubePos2H, cornerCubePos3H, roofMirrorPos2H, roofMirrorPos3H);
        return  position;
    }

    private int calculateCornerCubePosition(int wavelength, int nopa) throws IndexOutOfBoundsException {
        // Determine which calibration data to use
        ArrayList<int[]> calData;
        if (nopa == 2) {
            calData = calibrationData_2H;
        } else {
            calData = calibrationData_3H;
        }

        // The calibration data ArrayLists are sorted directly after they are read in by the parser,
        // so the adjacent wavelengths in the calibration data set can be easily looked up
        int i = 0;
        if (calData.get(i)[0] > wavelength) {
            throw new IndexOutOfBoundsException("Calibration data for the " + nopa + "H NOPA doesn't consider wavelengths this small.");
        }

        try {
            // i will be the index of the wavelength in the calibration file that is greater than the wavelength to be set
            // throws IndexOutOfBoundsException if the wavelength is greater than any of the values
            while (calData.get(i)[0] <= wavelength) {
                i++;
            }

            // Interpolates linearly between the two adjacent calibration data points
            double delta_calWavelength = calData.get(i)[0] - calData.get(i - 1)[0];
            double delta_calPosition = calData.get(i)[1] - calData.get(i - 1)[1];

            double slope = delta_calPosition / delta_calWavelength;
            int cornerCubePosition = (int) ((wavelength - calData.get(i - 1)[0]) * slope + calData.get(i - 1)[1]);

            return cornerCubePosition;
        } catch (IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("Calibration data for the " + nopa + "H NOPA doesn't consider wavelengths this great.");
        }
    }
}
