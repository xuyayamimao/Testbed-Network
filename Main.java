import java.io.File;
import java.io.FileWriter;

/**
 * Class that runs the experiment
 */
public class Main {
    public static void main(String[] args) throws Exception {
        FileWriter experimentOut = new FileWriter("IMIexperimentOut.txt");
        double initialTParameter = 0.1; //.3
        double NdAverage = 0.0;
        double b = 1.8;
        for (int j = 0; j < 7; j++){
            double toleranceP = PlayPDG.round((initialTParameter + j*0.1), 2); //.23
            experimentOut.write("Alpha Value: " + toleranceP + "\n");
            experimentOut.write("b Value: " + b + "\n");
            for (int i = 0; i < 10; i++){
                PlayPDG game = new PlayPDG(10000, 1.8,toleranceP, .1, i);
                double deadAgentPercent = (game.N.agentCount - game.N.aliveAgentCount)/(double)game.N.agentCount;
                experimentOut.write("Simulation" + i + " " + "Nd:" + deadAgentPercent + "\n");
                NdAverage += deadAgentPercent;
            }
            experimentOut.write("NdAverage:" + NdAverage/5 + "\n\n");
            NdAverage = 0.0;
        }
        experimentOut.close();
    }
}
