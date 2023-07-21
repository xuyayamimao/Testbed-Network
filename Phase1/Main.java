package Phase1;

import java.io.File;
import java.io.FileWriter;

/**
 * Class that runs the experiment
 */
public class Main {
    public static void main(String[] args) throws Exception {
        FileWriter experimentOut = new FileWriter("IMIexperimentOut.txt");
        double initialTParameter = 0.45; //.3
        double NdAverage = 0.0;
        double initialB = 1.6;
        for (int k = 0; k < 3; k++){
            double b = PlayPDG.round(initialB + k*0.2, 2);
            experimentOut.write("b Value: " + b + "\n");
            for (int j = 0; j < 10; j++){
                double toleranceP = PlayPDG.round((initialTParameter + j*0.01), 2); //.23
                experimentOut.write("Alpha Value: " + toleranceP + "\n");
                for (int i = 0; i < 10; i++){
                    System.out.println("new simulation");
                    PlayPDG game = new PlayPDG(10000, b ,toleranceP, .1, i);
                    System.out.println();
                    double deadAgentPercent = (game.N.agentCount - game.N.aliveAgentCount)/(double)game.N.agentCount;
                    experimentOut.write("Simulation" + i + " " + "Nd:" + deadAgentPercent + "\n");
                    NdAverage += deadAgentPercent;
                }
                experimentOut.write("NdAverage:" + NdAverage/10 + "\n\n");
                NdAverage = 0.0;
            }

        }

        experimentOut.close();
    }
}
