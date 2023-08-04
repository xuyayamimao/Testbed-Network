package Phase4;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.*;

import jdk.jfr.Percentage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.lang.Double;

/**
 * Class that runs the experiment
 */
public class AllMainQ {
    public static void main(String[] args) throws Exception {
        double initialAlpha;
        double initialB;

        for(int i = 0; i < 5; i++){//for loop for the four testing experiments
            XSSFWorkbook workbook = new XSSFWorkbook();
            switch (i) {
                case 0 -> {
                    initialAlpha = 0.0;
                    initialB = 0.0;
                }
                case 1 -> {
                    initialAlpha = 0.2;
                    initialB = 0.9;
                }
                case 2 -> {
                    initialAlpha = 0.2;
                    initialB = 1.0;
                }
                case 3 -> {
                    initialAlpha = 0.2;
                    initialB = 1.1;
                }
                case 4 -> {
                    initialAlpha = 0.2;
                    initialB = 2;
                }
                default -> throw new Exception("not possible");
            }
            String dir = System.getProperty("user.dir");
            //use otherData.txt to store the number of cascading failure within the simulations
            FileWriter otherData = new FileWriter(dir +  "/otherData.txt");

            //successfullData stores data of successful simulation
            ArrayList<Object[]> successfullData = new ArrayList<>();
            //failedData stores data of cascading failure simulation
            ArrayList<Object[]> failedData = new ArrayList<>();

            //create a new sheet in workbook to store data in successful simulations
            XSSFSheet spreadsheetSuccess = workbook.createSheet("Alpha" + initialAlpha + "b" + initialB+ "Success");
            //create a new sheet in workbook to store data in failed simulations
            XSSFSheet spreadsheetFail = workbook.createSheet("Alpha" + initialAlpha + "b" + initialB+ "Fail");
            int numOfCascadingFailure = 0;//initialize the number of cascading failure to be 0

            //write the headings of both data; note that the heading is in index 0 of both ArrayList
            successfullData.add(new Object[] {"Trial", "% of Alive Agents", "% of Activated Agents", "C|C %", "C|D %",
                    "D|D %", "Payoff Sum", "Payoff Sum if All Agents are Cooperators"});
            failedData.add(new Object[] {"Trial", "% of Alive Agents", "% of Activated Agents", "C|C %", "C|D %",
                    "D|D %", "Payoff Sum", "Payoff Sum if All Agents are Cooperators"});


            for (int m = 0; m < 100; m++) {//for loop for 100 simulations for each experiments
                if(Double.compare(initialB, 1.1) == 0 || Double.compare(initialB, 2.0) == 0){
                    AllPlayPDGQ game = new AllPlayPDGQ(10000, initialB, initialAlpha);
                    ArrayList<double[]> temp = game.Play(m);//temp stores all data of the game, refer PlayPDGQ class instance variable experimentData
                    if(m > 89) game.N.printNetworkToFile("Simulation" + m + "b"+initialB+"pajekFile.txt");
                    //for printing of alive agent percentage
                    System.out.println(temp.get(temp.size() - 1)[1]);
                    //compare element of index 1: alive agent percentage with 0, if same, then we have a cascading failure, so we add temp to failedData
                    if (Double.compare((temp.get(temp.size() - 1))[1], 0.0) == 0) {
                        addDataToList(failedData, temp);
                        numOfCascadingFailure++;
                    }//if there isn't cascading failure in this simulation, we add the data into successfulData
                    else {
                        addDataToList(successfullData, temp);
                    }
                }else{
                    AllPlayPDGQ game = new AllPlayPDGQ(10000, initialB, initialAlpha);
                    ArrayList<double[]> temp = game.Play(m);//temp stores all data of the game, refer PlayPDGQ class instance variable experimentData
                    //for printing of alive agent percentage
                    System.out.println(temp.get(temp.size() - 1)[1]);
                    //compare element of index 1: alive agent percentage with 0, if same, then we have a cascading failure, so we add temp to failedData
                    if (Double.compare((temp.get(temp.size() - 1))[1], 0.0) == 0) {
                        addDataToList(failedData, temp);
                        numOfCascadingFailure++;
                    }//if there isn't cascading failure in this simulation, we add the data into successfulData
                    else {
                        addDataToList(successfullData, temp);
                    }

                }
            }

            //calculating the average of data across all successful simulations and generate excel form
            for (int l = 1; l < successfullData.size(); l++) {
                Object[] trialData = successfullData.get(l);
                for (int k = 0; k < successfullData.get(2).length; k++) {
                    double b = (double) trialData[k];
                    trialData[k] = b /(100 - numOfCascadingFailure);

                }
            }
            int rowid = 0;
            writeDataToExcel(successfullData, spreadsheetSuccess, rowid);

            ////calculating the average of data across all failed simulations and generate excel form
            for (int l = 1; l < failedData.size(); l++) {
                Object[] trialData = failedData.get(l);
                for (int k = 0; k < failedData.get(2).length; k++) {
                    double b = (double) trialData[k];
                    trialData[k] = b /numOfCascadingFailure;

                }
            }
            writeDataToExcel(failedData, spreadsheetFail, rowid);

            otherData.write(numOfCascadingFailure + "\n");//write the number of cascading failures across 100 simulations to file
            FileOutputStream out = new FileOutputStream(dir + "/Q_Learning_learningR" + AllPlayPDGQ.learningR + "_"+"discountR" + AllPlayPDGQ.discountR + ".xlsx");
            workbook.write(out);
            out.close();
            System.out.println(successfullData.size());
            System.out.println(failedData.size());
            otherData.close();

        }
    }

    /**
     * This method write all data from ArrayList<Object[]>Data to excel
     * @param Data ArrayList<Object[]>
     * @param spreadsheet a spreadsheet within an Excel workbook
     * @param rowid for creating new row
     */
    private static void writeDataToExcel(ArrayList<Object[]>Data, XSSFSheet spreadsheet, int rowid) {
        XSSFRow rowFailed;
        for (Object[] datum : Data) {
            rowFailed = spreadsheet.createRow(rowid++);
            int cellid = 0;
            for (Object d : datum) {
                Cell cell = rowFailed.createCell(cellid++);
                if (d.getClass().equals(String.class)) {
                    cell.setCellValue((String) d);
                } else {
                    cell.setCellValue((double) d);
                }
            }
        }
    }

    /**
     * The method that add all data in temp to an ArrayList<Object[]> for writing to excel
     * @param Data ArrayList<Object[]> that stores the data being written to excel
     * @param temp ArrayList<double[]> stores the data generated by the game
     */

    private static void addDataToList(ArrayList<Object[]>Data, ArrayList<double[]> temp) {
        for(int k = 0; k < temp.size(); k++){
            Object[] array = convert(temp.get(k));
            if(Data.size() < k+2){
                Data.add(k+1, array);
            }else{
                for(int l = 0; l < array.length; l++){
                    double b = (double) Data.get(k+1)[l];
                    Data.get(k+1)[l] = b+ temp.get(k)[l];
                }
            }
        }
    }

    /**
     * This method convert an array of double to an array of Object
     * @param array array of double
     * @return array of Object
     */
    private static Object[] convert(double[] array) {
        Object[] output = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = array[i];
        }
        return output;
    }
}

