package Phase4;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Class that runs the experiment
 */
public class AllMainQ {
    public static void main(String[] args) throws Exception {
        double initialAlpha;
        double initialB;
        for(int i = 0; i < 4; i++){//for loop for the four testing experiments
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
            new File(dir + "/experimentAlpha" + initialAlpha + "b" + initialB).mkdirs();
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
            ArrayList<double[]> data = new ArrayList<>();
            int numOfCascadingFailure = 0;
            for(int j = 0; j < 100; j++){//for loop for 100 simulations for each experiments
                AllPlayPDGQ game = new AllPlayPDGQ(10000, initialB, initialAlpha);
                ArrayList<double[]> temp = game.Play(j);
                //if there's cascading failure in this simulation, we discard the data and update numOfCascading Failure
                if((temp.get(temp.size()-1))[0] == 0.0){
                    numOfCascadingFailure++;
                }//if there isn't cascading failure in this simulation, we add the data into data for calculating average and printing
                else{
                    for(int k = 0; k < temp.size(); k++){
                        double[] array = temp.get(k);
                        if(data.size() < k + 1){
                            data.add(array);
                        }else{
                            for(int l = 0; l < array.length; l++){
                                data.get(k)[l] += temp.get(k)[l];
                            }
                        }
                    }
                }
            }
            int round = 1;

            for (double[] datum : data) {
                trialNum.write(round + "\n");
                for (int k = 0; k < data.get(1).length; k++) {
                    datum[k] /= 100;
                    switch (k) {
                        case 0 -> aliveAgent.write(datum[k] + "\n");
                        case 1 -> RLAgent.write(datum[k] + "\n");
                        case 2 -> CCRecord.write(datum[k] + "\n");
                        case 3 -> CDRecord.write(datum[k] + "\n");
                        case 4 -> DDRecord.write(datum[k] + "\n");
                        case 5 -> payoffSum.write(datum[k] + "\n");
                        case 6 -> payoffSumIfAllCoop.write(datum[k] + "\n");
                        default -> throw new Exception("not possible");
                    }
                }
                round++;
            }//calculating the average of data across 100 trials and write corresponding data to file
            otherData.write(numOfCascadingFailure + "\n");//write the number of cascading failures across 100 simulations to file
            System.out.println(data.size());

            CDRecord.close();
            DDRecord.close();
            CCRecord.close();
            aliveAgent.close();
            payoffSum.close();
            payoffSumIfAllCoop.close();
            RLAgent.close();
            otherData.close();
            trialNum.close();
        }
    }
}
