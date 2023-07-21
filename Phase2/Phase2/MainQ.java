package Phase2;

import java.io.FileWriter;

/**
 * Class that runs the experiment
 */
public class MainQ {
    public static void main(String[] args) throws Exception {
        //FileWriter experimentOut = new FileWriter("IMIexperimentOut.txt");
        double initialAlpha = 0.45; //.3
        double NdAverage = 0.0;
        double initialB = 1.6;
        PlayPDGQ game = new PlayPDGQ(10, initialB, initialAlpha, 1);
        /*for (int k = 0; k < 3; k++){
            double b = PlayPDGQ.round(initialB + k*0.2, 2);
            experimentOut.write("b Value: " + b + "\n");
            for (int j = 0; j < 10; j++){
                double toleranceP = PlayPDGQ.round((initialAlpha + j*0.01), 2); //.23
                experimentOut.write("Alpha Value: " + toleranceP + "\n");
                for (int i = 0; i < 10; i++){
                    System.out.println("new simulation");
                    PlayPDGQ game = new PlayPDGQ(10000, b ,toleranceP, .1, i);
                    System.out.println();
                    double deadAgentPercent = (game.N.agentCount - game.N.aliveAgentCount)/(double)game.N.agentCount;
                    experimentOut.write("Simulation" + i + " " + "Nd:" + deadAgentPercent + "\n");
                    NdAverage += deadAgentPercent;
                }
                experimentOut.write("NdAverage:" + NdAverage/10 + "\n\n");
                NdAverage = 0.0;
            }

        }

        experimentOut.close();*/

    }
}
