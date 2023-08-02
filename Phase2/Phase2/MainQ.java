package Phase2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.*;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.ArrayUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Map;
import java.util.TreeMap;
import java.lang.Double;

/**
 * Class that runs the experiment
 */
public class MainQ {
    public static void main(String[] args) throws Exception {
        double initialAlpha;
        double initialB;
        for(int i = 0; i < 1; i++){//for loop for the four testing experiments
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
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFWorkbook workbook1 = new XSSFWorkbook();
            XSSFSheet spreadsheetSuccess = workbook.createSheet("experimentAlpha" + initialAlpha + "b" + initialB + "Success");
            XSSFSheet spreadsheetFail = workbook1.createSheet("experimentAlpha" + initialAlpha + "b" + initialB + "Fail");
            Map<Integer, Object[]> succesfullData = new TreeMap<>();
            Map<Integer, Object[]> failedData = new TreeMap<>();
            XSSFRow row;
            int numOfCascadingFailure = 0;
            succesfullData.put(1, new Object[] {"Trial", "Percentage of Alive Agent", "Percentage of Activated Agents", "CC Percentage", "CD Percentage",
            "DD Percentage", "Payoff Sum", "Payoff Sum if All Agents are Cooperators", "Percentage of Dormant Agent"});
            failedData.put(1, new Object[] {"Trial", "Percentage of Alive Agent", "Percentage of Activated Agents", "CC Percentage", "CD Percentage",
                    "DD Percentage", "Payoff Sum", "Payoff Sum if All Agents are Cooperators", "Percentage of Dormant Agent"});
            for(int j = 0; j < 10; j++){//for loop for 100 simulations for each experiments
                PlayPDGQ game = new PlayPDGQ(10000, initialB, initialAlpha);
                ArrayList<double[]> temp = game.Play(j);
                //if there's cascading failure in this simulation, we discard the data and update numOfCascading Failure
                if(Double.compare((temp.get(temp.size()-1))[0], 0.0) == 0){
                    for(int k = 0; k < temp.size(); k++){
                        Double[] array = ArrayUtils.toObject(temp.get(k));
                        if(failedData.size() < k + 2){
                            failedData.put(k+2, array);
                        }else{
                            for(int l = 0; l < array.length; l++){
                                double b = (double) failedData.get(k+2)[l];
                                failedData.get(k+2)[l] = b+ temp.get(k)[l];
                            }
                        }
                    }
                    numOfCascadingFailure++;
                }//if there isn't cascading failure in this simulation, we add the data into data for calculating average and printing
                else{
                    for(int k = 0; k < temp.size(); k++){
                        Double[] array = ArrayUtils.toObject(temp.get(k));
                        if(succesfullData.size() < k + 2){
                            succesfullData.put(k+2, array);
                        }else{
                            for(int l = 0; l < array.length; l++){
                                double b = (double) succesfullData.get(k+2)[l];
                                succesfullData.get(k+2)[l] = b+ temp.get(k)[l];
                            }
                        }
                    }
                }
            }
            int rowid = 0;

            for (int l = 2; l < succesfullData.size(); l++) {
                Object[] trialData = succesfullData.get(l);
                for (int k = 0; k < succesfullData.get(2).length; k++) {
                    double b = (double) trialData[k];
                    trialData[k] = b /(10 - numOfCascadingFailure);

                }
            }//calculating the average of data across 100 trials and write corresponding data to file
            for(Integer I : succesfullData.keySet()){
                row = spreadsheetSuccess.createRow(rowid++);
                Object[] array = succesfullData.get(I);
                int cellid = 0;
                for(Object d : array){
                    Cell cell = row.createCell(cellid++);
                    if(d.getClass().equals(String.class)){
                        cell.setCellValue((String) d);
                    }else{
                        cell.setCellValue((double) d);
                    }
                }
            }
            rowid=0;
            Row row1;

            for (int l = 2; l < succesfullData.size(); l++) {
                Object[] trialData = succesfullData.get(l);
                for (int k = 0; k < succesfullData.get(2).length; k++) {
                    double b = (double) trialData[k];
                    trialData[k] = b /numOfCascadingFailure;

                }
            }
            for(Integer I : failedData.keySet()){
                row1 = spreadsheetFail.createRow(rowid++);
                Object[] array = failedData.get(I);
                int cellid = 0;
                for(Object d : array){
                    Cell cell = row1.createCell(cellid++);
                    if(d.getClass().equals(String.class)){
                        cell.setCellValue((String) d);
                    }else{
                        cell.setCellValue((double) d);
                    }
                }
            }
            otherData.write(numOfCascadingFailure + "\n");//write the number of cascading failures across 100 simulations to file
            FileOutputStream out = new FileOutputStream(new File(dir + "/experimentAlpha" + initialAlpha + "b" + initialB +"Success.xlsx"));
            FileOutputStream out1 = new FileOutputStream(new File(dir + "/experimentAlpha" + initialAlpha + "b" + initialB +"Failed.xlsx"));
            workbook.write(out);
            workbook1.write(out1);
            out.close();
            out1.close();
            System.out.println(succesfullData.size());
            System.out.println(failedData.size());


            otherData.close();

        }
    }
}
