package Phase2;

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

    public static double learningR = 0.9;

    public static double discountR = 0.75;

    /**This constructor includes:
     * 1. Initializing a network with defectorPercent, generate2D4N network
     * 2. Create a directory based on simulationNum to store network after each round in text file (in Pajek format)
     * 3. Network continues playing PDG until reaching equilibrium state (either aliveAgentCount == 0, or cooperatorCount == aliveAgentCount)
     */
    public PlayPDGQ(int agentNum, double T, double alpha, double defectorPercent, int simulationNum) throws Exception {
        if(agentNum<5) throw new Exception("Agent number must be large than 4");
        N= new NetworkQ(agentNum);
        this.T = T;
        this.alpha = alpha;
        int i = 1;
        ArrayList<Double> eliminateRecord = new ArrayList<>();
        eliminateRecord.add(0.0);
        /*String dir = System.getProperty("user.dir");
        new File(dir + "/experiment" + alpha +"/simulation" + simulationNum).mkdirs();
        FileWriter NdRecord = new FileWriter(dir + "/experiment" + alpha +"/simulation" + simulationNum + "/NdRecord.txt", true);

         */
        while(!ifSteady(eliminateRecord) ) {

           // NdRecord.write("Round" + i);
            //NdRecord.write("Num of Coop: " + N.cooperatorCount);
            //System.out.println("Round" + i);
            //System.out.println("Num of Coop: " + N.cooperatorCount);
            //N.printNetworkToFile(dir + "/experiment" + alpha +"/simulation" + simulationNum + "/" + "trial" + i + ".txt");
            //NdRecord.write("Num of survived agemts:" + N.aliveAgentCount);
            //NdRecord.write("Num of Coop: " + N.cooperatorCount + "\n");
            System.out.println("Defector Num: " + (N.aliveAgentCount-N.cooperatorCount));
            calculatePayoffsAll();
            agentRemoveAll();
            //N.printAllData();
            strategyUpdateAll();

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
     * Enable the agent to Randomly choose a survived neighbor to imitate and update its own strategy
     * Probability for the agent to successfully imitate the picked neighbor's strategy depends
     * on the actualPayoff the agent received in the trial and the amount of actualPayoff the picked
     * neighbor received
     * @Return a boolean that mark the new strategy of the agent for the next trail
     */
    public boolean strategyUpdate(AgentQ a) {

            boolean result = a.getCooperate();
            int neighborNum = a.neighborNum();
            if(!a.isActivated()){ //if the agent is not activated but have at least one neighbor got activated, then activate the agent
                for(Integer i : a.getAdjLists()){
                    AgentQ b = N.agentsList.get(i);
                    if(b.isActivated() && !b.getEliminated()){
                        Random r = new Random();
                        if(r.nextInt(100) < 25){
                            a.activate();
                            break;
                        }
                    }
                }
            } else{
                genRTable(a);
                result = actionSelect(a);
            }
            return result;
    }

    /**
     * Select an action for the next round by choosing the action with
     * max overall Q-value
     * @param a AgentQ
     * @return boolean of whether agent is a cooperator
     */
    public boolean actionSelect(AgentQ a){
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


    /**
     * 1. Apply strategyUpdate(Network.Agent a) to all agents in the network.
     * 2. Skip agents that are already eliminated
     * 3. Modify cooperatorCount according to agents' updated strategy
     */
    public void strategyUpdateAll() {
        ArrayList<Boolean> newCoopList = new ArrayList<>();
        for (int i = 0; i < N.agentCount; i++) {
            AgentQ a = N.agentsList.get(i);
            if (!a.getEliminated()) {
                newCoopList.add(strategyUpdate(a));
            }else{
                newCoopList.add(a.getCooperate());
            }
        }
        for (int i = 0; i < N.agentCount; i++) {
            AgentQ a = N.agentsList.get(i);
               boolean originalBoo=a.getCooperate();
                a.setCooperate(newCoopList.get(i));
                if (a.getCooperate() == false && originalBoo == true) {
                    N.cooperatorCount--;
                } else if (a.getCooperate() == true && originalBoo == false) {
                    N.cooperatorCount++;
                }
        }
    }

    public void updateQTable(AgentQ a){
        boolean coop = a.getCooperate();
        for(Integer i : a.getAliveNeighbor()){
            if(coop){
                long newQValue = getNewQValue(a, i, 1);
                a.setQTable(i, 1, newQValue);
            }else{
                long newQValue = getNewQValue(a, i, 0);
                a.setQTable(i, 0, newQValue);
            }
        }
    }

    private static long getNewQValue(AgentQ a, int i, int coop) {
        long TD;
        long prev =  Math.max(a.getQTable()[a.getPrevState()][0], a.getQTable()[a.getPrevState()][1]);
        TD = (long) (a.getRTable()[i][coop] + discountR*prev - a.getQTable()[i][coop]);
        long newQValue = (long) (a.getQTable()[i][coop] + learningR*TD);
        return newQValue;
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
            }
        }
    }

    /**
     *
     * @param eliminateRecord is an ArrayList<Double> that stores percentage of eliminated agents in every round
     * @return a boolean of whether the network reaches a steady state
     */
    public boolean ifSteady(ArrayList<Double> eliminateRecord){
       int size = eliminateRecord.size();

        if ((eliminateRecord.get(size - 1).compareTo(1.0))==0){
            return true;
        }else if (size > 1){
            //System.out.println("arraysize: "+ size);
            if ((eliminateRecord.get(size - 1).compareTo(eliminateRecord.get(size - 2)))==0 &&
                    (N.aliveAgentCount == N.cooperatorCount || (N.aliveAgentCount > 0 && N.cooperatorCount == 0 ))){
                    //System.out.println("size-1: " +eliminateRecord.get(size - 1) );
                    //System.out.println("size-2: " +eliminateRecord.get(size - 2) );
                    return true;
                }
            }
        return false;
    }

    public void genRTable(AgentQ a){
        double[][] rTable = a.getRTable();
        for(int i = 0; i < rTable.length; i++){
            AgentQ neigh = N.agentsList.get(a.getAdjLists().get(i));
            if(!neigh.getEliminated()) {
                Boolean coop =neigh.getCooperate();
                for(int j = 0; j < rTable[0].length; j++){
                    if(j==0){  //if the agent is defector
                        if(coop==true){
                            a.setRTable(i, j, T);
                        }else{
                            a.setQTable(i, j, 0);
                        }
                    }else{ //cooperator
                        if(coop==true){
                            a.setRTable(i, j, 1);
                        }else{
                            a.setQTable(i, j, 0);
                        }
                    }
                }
            } else{
                continue;
            }

        }
    }

    /**
     * Round a value to value of precision
     * @param value double
     * @param precision
     * @return a double
     */
    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
