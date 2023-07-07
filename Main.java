import java.io.File;
import java.io.FileWriter;

/**
 * Class that runs the experiment
 */
public class Main {
    public static void main(String[] args) throws Exception {
        FileWriter experimentOut = new FileWriter("IMIexperimentOut.txt");
        double initialTParameter = 0.6; //.3
        double NdAverage = 0.0;
        for (int j = 0; j < 1; j++){
            double toleranceP = initialTParameter + j*0.1; //.23
            experimentOut.write("Alpha Value: " + toleranceP + "\n");
            for (int i = 0; i < 10; i++){
                PlayPDG game = new PlayPDG(10, 1.1,toleranceP, .1, i);
                double deadAgentPercent = (game.N.agentCount - game.N.aliveAgentCount)/(double)game.N.agentCount;
                experimentOut.write("Experiment" + i + " " + "Nd:" + deadAgentPercent + "\n");
                NdAverage += deadAgentPercent;
            }
            experimentOut.write("NdAverage:" + NdAverage/10 + "\n");
            NdAverage = 0.0;
        }
        experimentOut.close();
    }
}
