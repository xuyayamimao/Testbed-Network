package Phase2;
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
public class MainQ {
    public static void main(String[] args) throws Exception {
        double initialAlpha;
        double initialB;

        for(int i = 1; i < 4; i++){//for loop for the four testing experiments
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
                default -> throw new Exception("not possible");
            }
            String dir = System.getProperty("user.dir");
            //use otherData.txt to store the number of cascading failure within the simulations
            FileWriter otherData = new FileWriter(dir +  "/otherData.txt");


            /*
            FileWriter CCRecord = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB + "/CCRecord.txt");
            FileWriter CDRecord = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB + "/CDRecord.txt");
            FileWriter DDRecord = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB +  "/DDRecord.txt");
            FileWriter aliveAgent = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB  + "/aliveAgent.txt");
            FileWriter payoffSum = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB  + "/payoffSum.txt");
            FileWriter payoffSumIfAllCoop = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB + "/payoffSumIfAllCoop.txt");
            FileWriter RLAgent = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB + "/RLAgent.txt");
            //otherData stores the trialNum when all agents have become RL Agents
            FileWriter otherData = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB + "/otherData.txt");
            FileWriter trialNum = new FileWriter(dir + "/experimentAlpha" + initialAlpha + "b" + initialB + "/trialNum.txt");

             */
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
            "D|D %", "Payoff Sum", "Payoff Sum if All Agents are Cooperators", " % of Dormant Agents"});
            failedData.add(new Object[] {"Trial", "% of Alive Agents", "% of Activated Agents", "C|C %", "C|D %",
                    "D|D %", "Payoff Sum", "Payoff Sum if All Agents are Cooperators", " % of Dormant Agents"});


            for(int j = 0; j < 100; j++){//for loop for 100 simulations for each experiments
                PlayPDGQ game = new PlayPDGQ(10000, initialB, initialAlpha);
                ArrayList<double[]> temp = game.Play(j);//temp stores all data of the game, refer PlayPDGQ class instance variable experimentData
                //if there's cascading failure in this simulation, we add the data to failedData
                System.out.println(temp.get(temp.size()-1)[1]);
                //compare element of index 1: alive agent percentage with 0, if same, then we have a cascading failure
                if(Double.compare((temp.get(temp.size()-1))[1], 0.0) == 0){
                    addDatatoList(failedData, temp);
                    numOfCascadingFailure++;
                    //System.out.println("failed");
                }//if there isn't cascading failure in this simulation, we add the data into successfulData
                else{
                    addDatatoList(successfullData, temp);
                    //System.out.println("success");
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
            writeDatatoExcel(successfullData, spreadsheetSuccess, rowid);

            ////calculating the average of data across all failed simulations and generate excel form
            rowid=0;//reset row number
            for (int l = 1; l < failedData.size(); l++) {
                Object[] trialData = failedData.get(l);
                for (int k = 0; k < failedData.get(2).length; k++) {
                    double b = (double) trialData[k];
                    trialData[k] = b /numOfCascadingFailure;

                }
            }
            writeDatatoExcel(failedData, spreadsheetFail, rowid);

            otherData.write(numOfCascadingFailure + "\n");//write the number of cascading failures across 100 simulations to file
            FileOutputStream out = new FileOutputStream(dir + "/Hybrid_Q_Learning_learningR_" + PlayPDGQ.learningR + "discountR_" + PlayPDGQ.discountR + ".xlsx");
            workbook.write(out);
            out.close();
            System.out.println(successfullData.size());
            System.out.println(failedData.size());
            otherData.close();

        }
    }

    private static void writeDatatoExcel(ArrayList<Object[]>Data, XSSFSheet spreadsheet, int rowid) {
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

    private static void addDatatoList(ArrayList<Object[]>Data, ArrayList<double[]> temp) {
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

    private static Object[] convert(double[] array) {
        Object[] output = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = array[i];
        }
        return output;
    }
}

