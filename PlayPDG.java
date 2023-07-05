import java.io.File;
import java.util.List;

public class PlayPDG {
    /**Network N in Network class*/
    public Network N;

    /**In PDG, T>R>P>S*; T>1*/
    public static int R = 1;//reward of mutual cooperation
    public static int S = 0;//reward of cooperator being defected
    public static int P = 0;//reward of mutual defection
    public double T;//temptation to defect

    /** Tolerance Parameter alpha:
     * when tParameter = 1, agents have zero tolerance to elimination
     * when tParameter = 0, agents are completely tolerant;
     */
    public double tParameter;

    /** Normal Payoff for 2D4N network where all agents in the network are cooperators*/
    public int normalPayoff = 4;

    /**This constructor includes:
     * 1. Initializing a network with defectorPercent, generate2D4N network
     * 2. Create a directory based on expNum to store network after each round in text file (in Pajek format)
     * 3. Network continues playing PDG until reaching equilibrium state (either aliveAgentCount == 0, or cooperatorCount == aliveAgentCount)
     */
    public PlayPDG(int agentNum, double T, double toleranceP,double defectorPercent,int expNum) throws Exception {
        //use different tParameter as test cases
        if(agentNum<5) throw new Exception("Agent number must be large than 4");
        N= new Network(agentNum, defectorPercent);
        this.T = T;
        tParameter = toleranceP;
        N.generate2D4N(); //Generate the 2D4n network
        int i = 1;
        new File("C:/Users/chenjame/Desktop/Git/testbedNetwork/Testbed-Network/trialFileDirec" + expNum).mkdirs();

        while(N.aliveAgentCount != 0 ) {
            if (N.aliveAgentCount == N.cooperatorCount) {
                break;
            }
            System.out.println("Round" + i);
            System.out.println("Num of Coop: " + N.cooperatorCount);
            calculatePayoffsAll();
            agentRemoveAll();
            strategyUpdateAll();
            N.printNetworkToFile("C:/Users/chenjame/Desktop/Git/testbedNetwork/Testbed-Network/trialFileDirec"
                            + + expNum + "/" + "round" + i + ".txt");
            //N.printNetwork();
            i++;
            System.out.println("Num of survived agemts:" + N.aliveAgentCount);
            System.out.println("Num of Coop: " + N.cooperatorCount + "\n");

        }
        //counter for how many times an agent played w/ a neighbor? -good way for testing
    }

    /**
     * 1. Calculates payoffs received by an agent with index in one trail
     * 2. Update instance variable, actualPayoff, of the agent w/ the input index
     * @param index index of the agent in agentList
     */
    public void calculatePayoffs(int index){
        Network.Agent a = N.agentsList.get(index);
        double result = 0;
        for (Integer a1: a.adjLists){
            Network.Agent agent = N.agentsList.get(a1);
            boolean neighborCooperate = agent.getCooperate();
            if (a.getCooperate()){
                if (neighborCooperate){
                    result++;
                }
            }else{
                if (neighborCooperate){
                    result+=T;
                }
            }
        }//payoffs are the addition of all payoff with every neighbor
        result=round(result,2);//round the result to prevent java double calculation error
        a.setActualPayoffs(result);
    }

    /**
     * 1. Calculate and update actualPayoffs for all agents in the network in one trail
     * 2. Skip agents that are already eliminated
     */
    public void calculatePayoffsAll(){
        for(int i=0; i<N.agentCount; i++){
            if (!N.agentsList.get(i).getEliminated()){
                calculatePayoffs(i);
            }
            //System.out.println(N.agentsList.get(i).actualPayoffs);
        }
    }

    /**
     * Enable an agent to update its strategy
     * Enable the agent to Randomly choose a survived neighbor to imitate and update its own strategy
     * Probability for the agent to successfully imitate the picked neighbor's strategy depends
     * on the actualPayoff the agent received in the trial and the amount of actualPayoff the picked
     * neighbor received
     * @Return a boolean that mark the new strategy of the agent for the next trail
     */
    public boolean strategyUpdate(Network.Agent a) {
            boolean result = a.getCooperate();
            int neighborNum = a.neighborNum();
            int imiIndex = (int) Math.random() * neighborNum;//index of a randomly chosen neighbor
            int imiNeighbor = a.getAdjLists().get(imiIndex);
            Network.Agent b = N.agentsList.get(imiNeighbor); //the agent to imitate
            double noise = 0.1;//constant value of uncertainty in assessing payoff
            double Wij = 1 / (1 + Math.exp(-(b.getActualPayoffs() - a.getActualPayoffs()) / noise));
            if (Math.random() < Wij) {
                result = b.getCooperate();
            }
            return result;
    }

    /**
     * 1. Apply strategyUpdate(Network.Agent a) to all agents in the network.
     * 2. Skip agents that are already eliminated
     * 3. Modify cooperatorCount according to agents' updated strategy
     */
    public void strategyUpdateAll() {
        for (int i = 0; i < N.agentCount; i++) {
            Network.Agent a = N.agentsList.get(i);
            if (!a.getEliminated()) {
                boolean originalCoop = a.getCooperate();
                a.setCooperate(strategyUpdate(a));
                if (a.getCooperate() == false && originalCoop == true) {
                    N.cooperatorCount--;
                } else if (a.getCooperate() == true && originalCoop == false) {
                    N.cooperatorCount++;
                }
            }
        }
    }

    /**
     * 1. If an alive agent's actualPayoffs < tParameter*normalPayoff, it is eliminated
     * 2. Also remove all edges between the agent and its neighbors if the agent is eliminated
     * @param index index of the agent in Network.agentList
     * @return boolean of whether the agent is eliminated
     * @throws Exception
     */
    public boolean agentRemove(int index) throws Exception{
        Network.Agent a = N.agentsList.get(index);
        if (a.getActualPayoffs() < tParameter*normalPayoff && !a.getEliminated()){
            List<Integer> neighbors = a.getAdjLists(); //get all agents' neighbor's index
            for (Integer i: neighbors){
                Network.Agent ai = N.agentsList.get(i);
                ai.getAdjLists().remove((Integer) index);  //remove all edges between agent and its neighbor
            }
            return true;
        }
        return false;
    }

    /**
     * 1. Apply agentRemove() to all agents in the network
     * 2. Eliminate all agents with no neighbor
     * @throws Exception
     */
    public void agentRemoveAll() throws Exception {
        if(N.aliveAgentCount==0) throw new Exception("No agents in the network, can't remove agent. ");
        for (int i = 0; i < N.agentCount; i++){
            Network.Agent a = N.agentsList.get(i);
                if(agentRemove(i)){
                    a.setEliminated(true);
                    N.aliveAgentCount--;
                    if (a.getCooperate()){
                        N.cooperatorCount--;
                }
            }
        }

        for (int i = 0; i < N.agentCount; i++){
            Network.Agent a = N.agentsList.get(i);
            if (a.neighborNum() == 0 && !a.getEliminated()){
                a.setEliminated(true);
                N.aliveAgentCount--;
                if (a.getCooperate()){
                    N.cooperatorCount--;
                }
            }
        }
    }

    /**
     * Round a value to value of precision
     * @param value double
     * @param precision
     * @return a double
     */
    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
