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

    private PlayPDG(int agentNum, double T, double toleranceP,double defectorPercent) throws Exception {
        //use different tParameter as test cases
        if(agentNum<5) throw new Exception("Agent number must be large than 4");
        N= new Network(agentNum, defectorPercent);
        this.T = T;
        tParameter = toleranceP; //added 06/28
       // initializeNetwork(N, defectorPercent); //Initialize the network by make sure which agent cooperate&defect
        N.generate2D4N(); //Generate the 2D4n network
        N.printNetworkToFile("2D4N.txt");
        int i = 1;

        while(N.agentCount != 0) {
            if (N.agentCount == N.cooperatorCount) {
                break;
            }
            System.out.println("Round" + i);

            calculatePayoffsAll();
            agentRemoveAll();
            N.strategyUpdateAll();
            N.printNetworkToFile("2D4N.txt");
            i++;
            System.out.println("Num of Coop: " + N.cooperatorCount);

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
        for (Network.Agent a1: a.adjLists){
            boolean neighborCooperate = a1.getCooperate();
            if (a.getCooperate()){
                if (neighborCooperate){
                    result++;
                    //result=kahanSummation(1, result);
                }
            }else{
                if (neighborCooperate){
                    result+=T;
                    //result = kahanSummation(T, result);
                }
            }
        }
        result=round(result,2);
        a.actualPayoffs = result;

    }

    /**
     * Calculate all agents payoff in one trail
     */
    public void calculatePayoffsAll(){
        for(int i=0; i<N.agentCount; i++){
            calculatePayoffs(i);
            //System.out.println(N.agentsList.get(i).actualPayoffs);
        }
    }

    /**
     * Remove an agent out of the network if it's payoff(actualPayoffs) is less than tParameter*normalPayoff
     * Use this function when an agent is eliminated in a trial
     * Also eliminates its neighbors' Edge that connect to it in neighbors' adjLists
     * @param index Agent's index in agentsList
     */
    public Network.Agent agentRemove(int index) throws Exception{
        Network.Agent a = N.agentsList.get(index); //get out the agent
        if (a.actualPayoffs < tParameter*normalPayoff && !a.getEliminated()){
            List<Network.Agent> neighbors = a.getAdjLists(); //get all agents' neighbor's index
            for (Network.Agent i: neighbors){
                i.getAdjLists().remove(a);  //remove all connections between agent and its neighbor
            }
            return a;
        }
        return null;
    }


    public void agentRemoveAll() throws Exception {
        if(N.aliveAgentCount==0) throw new Exception("No agents in the network, can't remove agent. ");
        for (int i = 0; i < N.agentCount; i++){
            Network.Agent a = N.agentsList.get(i);
                if(agentRemove(i) != null){
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
        System.out.println("final agentCount" + N.agentCount);
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }


    public static void main(String[] args) throws Exception {
        PlayPDG game = new PlayPDG(10000, 1.3,0.3, .4);

    }





}
