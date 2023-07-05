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
        //



    }

    /** Initialize Network
     * Randomly place defectorPercent percentage of defectors in the network
     * Initialize rest of the agents as cooperators
     * The function only assign in random sequence of cooperative/defector behavior(randomly setting the
     * instance variable, cooperate, of each agent in the agentsList.
     * @param N Total number of agents player wants to have when initialize the network
     * @param defectorPercent percentage of initial defector player wants to have in the network
     */


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
            if (!N.agentsList.get(i).getEliminated()){
                calculatePayoffs(i);
            }
            //System.out.println(N.agentsList.get(i).actualPayoffs);
        }
    }

    /**
     * Function enable an agent to update its strategy
     * Randomly choose a survived neighbor to imitate and update strategy
     *
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
     * Remove an agent out of the network if it's payoff(actualPayoffs) is less than tParameter*normalPayoff
     * Use this function when an agent is eliminated in a trial
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

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }


    public static void main(String[] args) throws Exception {
        FileWriter experimentOut = new FileWriter("experimentOut.txt");
        double toleranceP = 0.25;
        for (int i = 1; i < 5; i++){
            PlayPDG game = new PlayPDG(10000, 1.1,toleranceP + i*0.05, .5, 2);
            experimentOut.write(i + " " + String.valueOf(game.tParameter) + "\n");
        }
        experimentOut.close();


    }
}
