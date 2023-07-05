import java.io.FileWriter;

/**
 * Class that runs the experiment
 */
public class Main {
    public static void main(String[] args) throws Exception {
        FileWriter experimentOut = new FileWriter("experimentOut.txt");
        double toleranceP = 0.4;
        for (int i = 10; i < 15; i++){
            PlayPDG game = new PlayPDG(10000, 1.1,toleranceP + (i-10)*0.05, .5, i);
            experimentOut.write(i + " " + String.valueOf(game.tParameter) + "\n");
        }
        experimentOut.close();
    }
}
