import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class PlayPDG {
    //Test
    private Network N;

    /*In PDG, T>R>P>S*; T>1*/
    private static int R = 1;//reward of mutual cooperation
    private static int S = 0;//reward of cooperator being defected
    private static int P = 0;//reward of mutual defection
    private double T;//temptation to defect //int->double 06/29 T-> something else (b)

    /** Tolerance Parameter alpha;
     * when tParameter = 1, agents have zero tolerance to elimination
     * when tParameter = 0, agents are completely tolerant;
     */
    private double tParameter;

    /** Normal Payoff for 2D4N network
     * where all agents in the network are cooperators
     */
    private int normalPayoff = 4;

    /**
     * Include initializing a network, generate2D4N(both originate from Network class)
     * Create a directory based on expNum to put in the output of network after each trial(txt file that
     * contain form that is acceptable as input file for pajek(need to transform txt to NET file first, though)
     * Will stop when the after a certain number of trial, the network reaches an equilibrium state
     */
    private PlayPDG(int agentNum, double T, double toleranceP,double defectorPercent,int expNum) throws Exception {
        //use different tParameter as test cases
        if(agentNum<5) throw new Exception("Agent number must be large than 4");
        N= new Network(agentNum, defectorPercent);
        this.T = T;
        tParameter = toleranceP; //added 06/28
       // initializeNetwork(N, defectorPercent); //Initialize the network by make sure which agent cooperate&defect
        N.generate2D4N(); //Generate the 2D4n network
       // N.printNetworkToFile("2D4N.txt");
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
     * Calculate payoff receive by an agent in one trail
     * Update instance variable, actualPayoff, of the agent w/ the input index
     * @param index index of the agent in agentList we are currently iterating through
     */
    public void calculatePayoffs(int index){  //index means the index of agent in agentsList
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
        }
        result=round(result,2);
        a.setActualPayoffs(result);
    }

    /**
     * Calculate all agents payoff in one trail
     */
    public void calculatePayoffsAll(){
        for(int i=0; i<N.agentCount; i++){
            if (!N.agentsList.get(i).getEliminated()){ //only calculate payoffs for agents that are not eliminated
                calculatePayoffs(i);
            }
            //System.out.println(N.agentsList.get(i).actualPayoffs);
        }
    }

    /**
     * Enable an agent to update its strategy (eg: cooperator ->defector)
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
     * apply strategyUpdate(Network.Agent a) to all agents in the network.
     * Modify value of cooperatorCount based on each agent's strategy before their
     * strategyUpdate() and their strategy after it.
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
     * Return a boolean that indicate whether an agent should remove out of the network if it's payoff(actualPayoffs)
     * is less than tParameter*normalPayoff
     * Also eliminates its neighbors' Edge that connect to it in neighbors' adjLists
     * @param index Agent's index in agentsList
     */
    public boolean agentRemove(int index) throws Exception{
        Network.Agent a = N.agentsList.get(index); //get out the agent
        if (a.getActualPayoffs() < tParameter*normalPayoff && !a.getEliminated()){
            List<Integer> neighbors = a.getAdjLists(); //get all agents' neighbor's index
            for (Integer i: neighbors){
                Network.Agent ai = N.agentsList.get(i);
                ai.getAdjLists().remove((Integer) index);  //remove all connections between agent and its neighbor
            }
            return true;
        }
        return false;
    }

    /**
     * apply agentRemove() to all agents in the network. The function also eliminate an agent if it
     * has zero neighbors after a trial.
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
     * round a value to value of precision
     * @param value
     * @param precision
     * @return
     */
    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }


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
