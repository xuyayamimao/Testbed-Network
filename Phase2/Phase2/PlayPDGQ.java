package Phase2;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class PlayPDGQ {
    /**Network N in Network class*/
    public NetworkQ N;

    /**In PDG, T>R>P>S*; T>1*/
    public static int R = 1;//reward of mutual cooperation
    public static int S = 0;//reward of cooperator being defected
    public static int P = 0;//reward of mutual defection
    public double T;//temptation to defect

    /** Tolerance Parameter alpha:
     * when tParameter = 1, agents have zero tolerance to elimination
     * when tParameter = 0, agents are completely tolerant;
     */
    public double alpha;

    /** Normal Payoff for 2D4N network where all agents in the network are cooperators*/
    public int normalPayoff = 4;

    /**
     * Learning rate in RL
     */
    public static double learningR = 0.9;

    /**
     * Discount rate in RL
     */
    public static double discountR = 0.75;

    /**
     * Percentage of explore of an RL agent
     */
    public static double exploreR = 0.1;

    /**
     * the chance of an dormant agent being activated when it has at least one activated neighbor
     */
    public static int activateChance = 25;

    /**This constructor includes:
     * 1. Initializing a network with defectorPercent, generate2D4N network
     * 2. Create a directory based on simulationNum to store network after each round in text file (in Pajek format)
     * 3. Network continues playing PDG until reaching equilibrium state (either aliveAgentCount == 0, or cooperatorCount == aliveAgentCount)
     */
    public PlayPDGQ(int agentNum, double T, double alpha, int simulationNum) throws Exception {
        if(agentNum<5) throw new Exception("Agent number must be large than 4");
        N= new NetworkQ(agentNum);
        this.T = T;
        this.alpha = alpha;
        int i = 1;
        ArrayList<Double> eliminateRecord = new ArrayList<>();
        eliminateRecord.add(0.0);
        String dir = System.getProperty("user.dir");
        new File(dir + "/experiment" + alpha +"/simulation" + simulationNum).mkdirs();
        //FileWriter NdRecord = new FileWriter(dir + "/experiment" + alpha +"/simulation" + simulationNum + "/NdRecord.txt", true);
        FileWriter CDRecord = new FileWriter(dir + "/experiment" + alpha +"/simulation" + simulationNum + "/CDRecord.txt");
        while(!ifSteady(eliminateRecord) ) {
           // NdRecord.write("Round" + i);
            //NdRecord.write("Num of Coop: " + N.cooperatorCount);
            System.out.println("Round" + i);
            //System.out.println("Num of Coop: " + N.cooperatorCount);
            //N.printNetworkToFile(dir + "/experiment" + alpha +"/simulation" + simulationNum + "/" + "trial" + i + ".txt");
            //NdRecord.write("Num of survived agemts:" + N.aliveAgentCount);
            //NdRecord.write("Num of Coop: " + N.cooperatorCount + "\n");
            //System.out.println("Defector Num: " + (N.aliveAgentCount-N.cooperatorCount));
            calculatePayoffsAll();
            CDRecord.write("CD: " + printCDPair() +"\n");
            agentRemoveAll();
            updateRTableAll();
            updateQTableAll();
            N.printAllData();
            strategyUpdateAll();
            //N.printAllData();
            System.out.println(N.RLAgentList.size());

            double deadAgentPercent = ((double)N.agentCount - (double)N.aliveAgentCount)/(double)N.agentCount;
            eliminateRecord.add(deadAgentPercent);
            //NdRecord.write(deadAgentPercent + "\n");

            //N.printNetwork();
            i++;


            //System.out.println("Num of survived agemts:" + N.aliveAgentCount);
            //System.out.println("Num of Coop: " + N.cooperatorCount + "\n");

        }
        //counter for how many times an agent played w/ a neighbor? -good way for testing
        //NdRecord.close();
        CDRecord.close();
    }

    /**
     * 1. Calculates payoffs received by an agent with index in one trail
     * 2. Update instance variable, actualPayoff, of the agent w/ the input index
     * @param index index of the agent in agentList
     */
    public void calculatePayoffs(int index){
        AgentQ a = N.agentsList.get(index);
        double result = 0;
        for (Integer a1: a.getAdjLists()){
            AgentQ agentQ = N.agentsList.get(a1);
            boolean neighborCooperate = agentQ.getCooperate();
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
     * If the agent is not activated, and it has at least activated neighbor -> the agent has dice% chance or being activated
     * If the agent is activated, then it either exploits or explores
     * @param a AgentQ
     * @return
     */
    public int strategyUpdate(AgentQ a) {
            int result = (a.getCooperate()) ? 1 : 0;
            if(!a.isActivated()){ //if the agent is not activated but have at least one neighbor got activated, then activate the agent
                for(Integer i : a.getAdjLists()){
                    AgentQ b = N.agentsList.get(i);
                    if(b.isActivated() && !b.getEliminated()){
                        Random r = new Random();
                        if(r.nextInt(100) < activateChance){
                            result = 2;
                        }//if at least one of AgentQ a's neighbor is activated, a has 25% chance of being activated
                        break;
                    }
                }
            } else{
                //if AgentQ is RL agent, then roll the dice and either explore or exploit
                Random r = new Random();
                int dice = r.nextInt(100);
                if(dice < 100*exploreR){
                    result = (explore()) ? 1 : 0;
                }else{
                    result = (exploit(a)) ? 1 : 0;
                }
            }
            return result;
    }

    /**
     * Select an action for the next round by choosing the action with
     * max overall Q-value
     * @param a AgentQ
     * @return boolean of whether agent is a cooperator
     */
    public boolean exploit(AgentQ a){
        int sumCoop = 0, sumDefect = 0;
        boolean result;
        for(int i = 0; i < 4; i++){
           sumDefect += a.getQTable()[i][0];
           sumCoop += a.getQTable()[i][1];
        }
        if(sumCoop > sumDefect){
            result = true;
        } else if (sumCoop < sumDefect) {
            result = false;
        }else{
            Random r = new Random();
            result = r.nextBoolean();
        }
        return result;
    }

    public boolean explore(){
        Random r = new Random();
        return r.nextBoolean();
    }


    /**
     * 1. Apply strategyUpdate(Network.Agent a) to all agents in the network.
     * 2. Skip agents that are already eliminated
     * 3. Modify cooperatorCount according to agents' updated strategy
     */
    public void strategyUpdateAll() {
        ArrayList<Integer> newCoopList = new ArrayList<>();

        for (int i = 0; i < N.agentCount; i++) {
            AgentQ a = N.agentsList.get(i);
            if (!a.getEliminated()) {
                newCoopList.add(strategyUpdate(a));
            }else{
                newCoopList.add((a.getCooperate()) ? 1 : 0);
            }
        }
        for (int i = 0; i < N.agentCount; i++) {
            AgentQ a = N.agentsList.get(i);
               boolean originalBoo=a.getCooperate();
               if(newCoopList.get(i) == 0){
                   a.setCooperate(false);
               }else if(newCoopList.get(i) == 1){
                   a.setCooperate(true);
               }else{
                   a.activate();
                   N.RLAgentList.add(i);//if activated, update RLAgentList
               }
                if (!a.getCooperate() && originalBoo) {
                    N.cooperatorCount--;
                } else if (a.getCooperate() && !originalBoo) {
                    N.cooperatorCount++;
                }
        }
    }

    /**
     * Update the Q-Table of AgentQ a in one round of the game
     * @param a AgentQ
     */
    public void updateQTable(AgentQ a){
        boolean coop = a.getCooperate();
        for(int i = 0; i < 4; i++){
            if(!a.getQNeighborList().get(i).getEliminated()){//if the neighbor corresponding to a state is not eliminated
                a.setPrevState(i);//we update the Agent's previous state as i
                if(coop){
                    long newQValue = getNewQValue(a, i, 1);
                    a.setQTable(i, 1, newQValue);
                }else{
                    long newQValue = getNewQValue(a, i, 0);
                    a.setQTable(i, 0, newQValue);
                }
            }//if the neighbor corresponding to the state i is eliminated, we jump to the next state to update Q-Value
        }
    }

    public void updateQTableAll(){
        for(int i = 0; i < N.RLAgentList.size(); i++){
            int index = N.RLAgentList.get(i);
            AgentQ agentQ = N.agentsList.get(index);
            updateQTable(agentQ);
        }
    }

    /**
     * get the new Q-Value in a state-action pair
     * @param a AgentQ
     * @param i the state AgentQ a is in
     * @param coop action of the AgentQ, 0 if defector, 1 if cooperator
     * @return long value of the new Q-Value
     */
    private static long getNewQValue(AgentQ a, int i, int coop) {
        long TD;
        long prev =  Math.max(a.getQTable()[a.getPrevState()][0], a.getQTable()[a.getPrevState()][1]);
        TD = (long) (a.getRTable()[i][coop] + discountR*prev - a.getQTable()[i][coop]);
        return (long) (a.getQTable()[i][coop] + learningR*TD);
    }

    /**
     * 1. If an alive agent's actualPayoffs < tParameter*normalPayoff, it is eliminated
     * 2. Also remove all edges between the agent and its neighbors if the agent is eliminated
     * @param index index of the agent in Network.agentList
     * @return boolean of whether the agent is eliminated
     * @throws Exception
     */
    public boolean agentRemove(int index) throws Exception{
        AgentQ a = N.agentsList.get(index);
        if (a.getActualPayoffs() < alpha*normalPayoff && !a.getEliminated()){
            List<Integer> neighbors = a.getAdjLists(); //get all agents' neighbor's index
            for (Integer i: neighbors){
                AgentQ ai = N.agentsList.get(i);
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
            AgentQ a = N.agentsList.get(i);
                if(agentRemove(i)){
                    a.setEliminated(true);
                    N.aliveAgentCount--;
                    if (a.getCooperate()){
                        N.cooperatorCount--;
                }
                    if(a.isActivated()){
                        N.RLAgentList.remove((Integer) i);
                    }//if the removed agent is an RL agent, update RLAgentList
            }

        }

        for (int i = 0; i < N.agentCount; i++){
            AgentQ a = N.agentsList.get(i);
            if (a.neighborNum() == 0 && !a.getEliminated()){
                a.setEliminated(true);
                N.aliveAgentCount--;
                if (a.getCooperate()){
                    N.cooperatorCount--;
                }
                if(a.isActivated()){
                    N.RLAgentList.remove((Integer) i);
                }//if the removed agent is an RL agent, update RLAgentList
            }
        }
    }


    public void updateRTable(AgentQ a){
        double[][] rTable = a.getRTable();
        for(int i = 0; i < rTable.length; i++){
            AgentQ neigh = a.getQNeighborList().get(i);
            if(!neigh.getEliminated()) {
                boolean coop =neigh.getCooperate();
                for(int j = 0; j < rTable[0].length; j++){
                    if(j==0){  //if the agent a is defector
                        if(coop){//if the neighbor is cooperator
                            a.setRTable(i, j, T);
                        }else{//if the neighbor is a defector
                            a.setQTable(i, j, 0);
                        }
                    }else{ //if the agent a is a cooperator
                        if(coop){//if the neighbor is cooperator
                            a.setRTable(i, j, 1);
                        }else{//if the neighbor is a defector
                            a.setQTable(i, j, 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * Update the R-Table for all RL agents for them to calculate Q-Value
     */
    public void updateRTableAll(){
        for(int i = 0; i < N.RLAgentList.size(); i++){
            int index = N.RLAgentList.get(i);
            AgentQ agentQ = N.agentsList.get(index);
            updateRTable(agentQ);
        }
    }

    /**
     * Round a value to value of precision
     * @param value double
     * @param precision
     * @return a double
     */

    /**
     *
     * @param eliminateRecord is an ArrayList<Double> that stores percentage of eliminated agents in every round
     * @return a boolean of whether the network reaches a steady state
     */
    public boolean ifSteady(ArrayList<Double> eliminateRecord){
        //1. if all are dead
        //2. if all agents have become RL agents && their clock has expired (int trainingPeriod) ; have a counter of how many RL agent's clock has expired
        //3. if all alive agents are Rl agents && percentage of dead agents remain the same for 1000 trials
        //have a counter to the number of trials percentage of dead agents stays the same, continue playing until counter reaches 1000/reset counter when current is different from previous
        int size = eliminateRecord.size();
        if ((eliminateRecord.get(size - 1).compareTo(1.0))==0){
            return true;
        }

        return false;
    }

    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public int printCDPair(){
        int count = 0;
        for(int i = 0; i < N.agentCount; i++){
            AgentQ agentQ = N.agentsList.get(i);
            if(agentQ.getCooperate()){
                for(Integer j : agentQ.getAdjLists()){
                    AgentQ neighbor = N.agentsList.get(j);
                    if(!neighbor.getCooperate()){
                        count++;
                    }
                }
            }else{
                for(Integer j : agentQ.getAdjLists()){
                    AgentQ neighbor = N.agentsList.get(j);
                    if(neighbor.getCooperate()){
                        count++;
                    }

            }
        }
    }
        return count/2;
}
}
