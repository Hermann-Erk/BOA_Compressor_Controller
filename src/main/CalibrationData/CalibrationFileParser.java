package main.CalibrationData;

import main.Constants;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Hermann on 13.09.2017.
 */

/**
 * This class reads in the calibration files
 */
public class CalibrationFileParser {
    private ArrayList<int[]> calibrationData = new ArrayList<>();

    /**
     * Reads in the given file and returns an ArrayList of calibration data. Every entry is an array of two values,
     * the wavelength and the cornercube position. There should be separate files for the 2H and 3H NOPA.
     *
     * @param filepathString String that represents the file to read
     * @return ArrayList of arrays (2 integer) of the respective wavelengths and cornercube positions to minimize the
     * GVD
     * @throws IOException
     */
    public ArrayList<int[]> readCalibrationFile(String  filepathString) throws IOException {
        calibrationData = new ArrayList<>();
        File filepath = new File(filepathString);
        try (Scanner scanner =  new Scanner(filepath)){
            while (scanner.hasNextLine()){
                processLine(scanner.nextLine());
            }
        }

        sortData(calibrationData);
        return calibrationData;
    }

    private void processLine(String line) {
        //use a second Scanner to parse the content of each line
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter("\t");
        if (scanner.hasNext()) {
            try {
                int wavelength = scanner.nextInt();
                int cornerCubePosition = scanner.nextInt();
                int dataSet[] = {wavelength, cornerCubePosition};
                calibrationData.add(dataSet);
            }catch (InputMismatchException e){
                //Is thrown for the Header, can be ignored
                //e.printStackTrace();
            }
        } else {
            //System.out.println("Empty or invalid line. Unable to process.");
        }
    }

    public static void printCalibrationDataToConsole(ArrayList<int[]> list){
        for (int[] intPair: list) {
            System.out.println("wl: " + intPair[0] + " pos: " + intPair[1]);
        }
    }

    //Sort the ArrayList using the first index (the wavelength) of the data pairs
    private static void sortData(ArrayList<int[]> data){
        Collections.sort(data, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(a[0], b[0]);
            }
        });
    }
}
