import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayPDG {
    private Network N;

    /*In PDG, T>R>P>S*; T>1*/
    private static int R = 1;//reward of mutual cooperation
    private static int S = 0;//reward of cooperator being defected
    private static int P = 0;//reward of mutual defection
    private int T;//temptation to defect

    /** Tolerance Parameter alpha;
     * when tParameter = 1, agents have zero tolerance to elimination
     * when tParameter = 0, agents are completely tolerant;
     */
    private int tParameter;

    /** Normal Payoff for 2D4N network
     * where all agents in the network are cooperators
     */
    private int normalPayoff = 4;

    private PlayPDG(Network N, int T, int toleranceP){
        this.N = N;
        this.T = T;
    }

    /** Initialize Network
     * Randomly place defectorPercent percentage of defectors in the network
     * Initialize rest of the agents as cooperators
     */
    private void initializeNetwork(Network N, double defectorPercent){
        List<Boolean> ifCooperate = new ArrayList<Boolean>();
        int numTrue = (int) Math.round(N.agentCount * defectorPercent);
        for (int i = 0; i < numTrue; i++){
            ifCooperate.add(true);
        }
        for (int i = 0; i < N.agentCount - numTrue; i++){
            ifCooperate.add(false);
        }
        Collections.shuffle(ifCooperate);
        for (int i = 0; i < N.agentCount; i++){
          N.agentsList.get(i).setCooperate(ifCooperate.get(i));
        }
    }





}
