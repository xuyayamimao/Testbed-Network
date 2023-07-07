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
        for (int j = 0; j < 4; j++){
            double toleranceP = initialTParameter + j*0.1; //.23
            new File("C:/Users/chenjame/Desktop/Git/testbedNetwork/Testbed-Network/alpha" + toleranceP).mkdirs();
            experimentOut.write("Alpha Value: " + toleranceP + "\n");
            for (int i = 0; i < 10; i++){
                PlayPDG game = new PlayPDG(100, 1.05,toleranceP, .1, i);
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
