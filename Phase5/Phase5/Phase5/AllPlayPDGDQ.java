package Phase5;

import java.util.*;

public class AllPlayPDGDQ {
    /**
     * Network N in Network class
     */
    public AllNetworkDQ N;

    /**
     * In PDG, T>R>P>S*; T>1
     */
    public static int R = 1;//reward of mutual cooperation
    public static int S = 0;//reward of cooperator being defected
    public static int P = 0;//reward of mutual defection
    public double T;//temptation to defect

    /**
     * Tolerance Parameter alpha:
     * when tParameter = 1, agents have zero tolerance to elimination
     * when tParameter = 0, agents are completely tolerant;
     */
    public double alpha;

    /**
     * Normal Payoff for 2D4N network where all agents in the network are cooperators
     */
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
    public static double exploreR = 0.1;//0.1


    public static int clockPeriod = 300;

    /**
     * Counter of how many RL agents' clock has expired
     * Increment if an alive agent's clock expired
     * Decrement if a RL agent with expired clock is removed
     */
    public int expiredClockCount;

    public int trialNum;

    /**
     * experimentData stores all experiment data we are collecting, every index in ArrayList represent a trial
     * double[0] = aliveAgent percentage
     * double[1] = RLAgent percentage
     * double[2] = CC percentage
     * double[3] = CD percentage
     * double[4] = DD percentage
     * double[5] = payoffSum
     * double[6] = payoffSumIfAllCoop
     */
    ArrayList<double[]> experimentData;


    /**
     * Constructor of a new PlayPDGQ object
     * @param agentNum number of agents in the network
     * @param initialB b value
     * @param initialAlpha alpha value
     * @throws Exception
     */
    public AllPlayPDGDQ(int agentNum, double initialB, double initialAlpha) throws Exception {
        if (agentNum < 5) throw new Exception("Agent number must be large than 4");
        N = new AllNetworkDQ(agentNum);
        this.T = initialB;
        this.alpha = initialAlpha;
        expiredClockCount = 0;
        trialNum = 0;
        experimentData = new ArrayList<>();
    }

    /**
     * Instance method that actually plays the game and return all game data of all trials
     * @param simulationNum the current simulation
     * @return an ArrayList<double[]> that stores all the data of all trials
     * @throws Exception
     */
    public ArrayList<double[]> Play(int simulationNum) throws Exception {
        //ArrayList<Double> eliminateRecord = new ArrayList<>();
        //eliminateRecord.add(0.0);
        //AgentDQ firstRl = N.agentsList.get(N.RLAgentList.get(0));
        //boolean firstRLCoop = firstRl.getCooperate();
        int round = 1;//counter of rounds


        while (round <= 2000) {
            //System.out.println("Round" + round);
            double[] array = new double[7];
            double[] CDCCDDPairPercent = printCDCCDDPairPercent();
            array[0] = (double)N.aliveAgentCount/N.agentCount;//aliveAgent percentage
            array[1] = (double)N.RLAgentList.size()/N.agentCount;//RLAgent percentage
            array[2] = CDCCDDPairPercent[1];//CC
            array[3] = CDCCDDPairPercent[0];//CD
            array[4] = CDCCDDPairPercent[2];//DD
            array[5] = calculatePayoffsAll();//payoffSum
            array[6] = CDCCDDPairPercent[3];//payoffSumIfAllCoop
            experimentData.add(array);
            //System.out.println(firstRLCoop);
            //firstRl.printQTableA();
            //firstRl.printQTableB();
            //firstRl.printRTable();
            agentRemoveAll();
            updateRTableAll();
            updateQTableAll();
           /*
            System.out.println("alive agent num: " + N.aliveAgentCount);
            System.out.println("RL agent num: " + N.RLAgentList.size());
            System.out.println("expired clock num " + expiredClockCount);
            System.out.println("cooperator count: " +N.cooperatorCount);
            N.printAllData();
            */
            strategyUpdateAll();
            double deadAgentPercent = ((double) N.agentCount - (double) N.aliveAgentCount) / (double) N.agentCount;
            //eliminateRecord.add(deadAgentPercent);
            round++;
        }
        return experimentData;
    }

    /**
     * 1. Calculates payoffs received by an agent with index in one trail
     * 2. Update instance variable, actualPayoff, of the agent w/ the input index
     *
     * @param index index of the agent in agentList
     */
    public double calculatePayoffs(int index) {
        AllAgentDQ a = N.agentsList.get(index);
        double result = 0;
        for (Integer a1 : a.getAdjLists()) {
            AllAgentDQ agentQ = N.agentsList.get(a1);
            boolean neighborCooperate = agentQ.getCooperate();
            if (a.getCooperate()) {
                if (neighborCooperate) {
                    result++;
                }
            } else {
                if (neighborCooperate) {
                    result += T;
                }
            }
        }//payoffs are the addition of all payoff with every neighbor
        result = round(result, 2);//round the result to prevent java double calculation error
        a.setActualPayoffs(result);
        return result;
    }

    /**
     * 1. Calculate and update actualPayoffs for all agents in the network in one trail
     * 2. Skip agents that are already eliminated
     */
    public double calculatePayoffsAll() {
        double sumOfPayoffsAll = 0;
        for (int i = 0; i < N.agentCount; i++) {
            if (!N.agentsList.get(i).getEliminated()) {
                sumOfPayoffsAll += calculatePayoffs(i);

            }
            //System.out.println(N.agentsList.get(i).actualPayoffs);
        }
        return sumOfPayoffsAll;
    }

    /**
     * 1. Update all RL agents' strategy, modify cooperatorCount according to agents' updated strategy
     * 2. Update all RL agents' non-RL neighbors' strategy according to activateChance, modify cooperatorCount
     */
    public void strategyUpdateAll() {
        //update all RL agents' strategy
        for (int i = 0; i < N.RLAgentList.size(); i++) {
            boolean result;
            int index = N.RLAgentList.get(i);
            AllAgentDQ allAgentDQ = N.agentsList.get(index);
            boolean originBool = allAgentDQ.getCooperate();
            if (allAgentDQ.getClock() > -1) {//when the clock hasn't expired
                allAgentDQ.incrementClock();
                Random r = new Random();
                int dice = r.nextInt(100);
                if (dice < 100 * exploreR) {
                    result = explore();
                } else {
                    result = exploit(allAgentDQ);
                }
                if (allAgentDQ.incrementClock() > clockPeriod) {
                    allAgentDQ.expireClock();//if clock in the next round is larger than the set clockPeriod, the clock is expired and set to -1
                    expiredClockCount++;//update the number of expired clock
                }
            } else {//when the clock has expired
                result = exploit(allAgentDQ);
            }

            //update above agents' cooperate value, and update cooperateCount
            allAgentDQ.setCooperate(result);
            if (originBool && !allAgentDQ.getCooperate()) {
                N.cooperatorCount--;
            } else if (!originBool && allAgentDQ.getCooperate()) {
                N.cooperatorCount++;
            }

        }
    }

    /**
     * Select an action for the next round by choosing the action with
     * max overall Q-value
     *
     * @param a AgentQ
     * @return boolean of whether agent is a cooperator
     */
    public boolean exploit(AllAgentDQ a) {
        Random s = new Random();
        int QTable = s.nextInt(2);
        double[][] QTableCopy = new double[4][2];

        if(QTable == 0){
            QTableCopy = a.getQTableA();
        }else{
            QTableCopy = a.getQTableB();
        }
        int sumCoop = 0, sumDefect = 0;
        boolean result;

        for (int i = 0; i < 4; i++) {
            sumDefect += QTableCopy[i][0];
            sumCoop += QTableCopy[i][1];
        }
        if (sumCoop > sumDefect) {
            result = true;
        } else if (sumCoop < sumDefect) {
            result = false;
        } else {
            Random r = new Random();
            result = r.nextBoolean();
        }


        return result;
    }

    public boolean explore() {
        Random r = new Random();
        return r.nextBoolean();
    }

    /**
     * Update the Q-Table of AgentQ a in one round of the game
     *
     * @param a AgentQ
     */
    public void updateQTable(AllAgentDQ a) {
        Random r = new Random();
        int QTable = r.nextInt(2);
        boolean coop = a.getCooperate();
        for (int i = 0; i < 4; i++) {
            if (!a.getQNeighborList().get(i).getEliminated()) {//if the neighbor corresponding to a state is not eliminated
                if (coop) {
                    if(QTable == 0){//when QTable ==0, we choose A as the current QTable
                        double newQValue = getNewQValue(a, i, 1, a.getQTableA(), a.getQTableB());
                        a.setQTableA(i, 1, newQValue);
                    }else{//when QTable == 1, we choose B as the current QTable
                        double newQValue = getNewQValue(a, i, 1, a.getQTableB(), a.getQTableA());
                        a.setQTableB(i, 1, newQValue);
                    }
                } else {  //defector
                    if(QTable == 0){//A as the current QTable
                        double newQValue = getNewQValue(a, i, 0, a.getQTableA(), a.getQTableB());
                        a.setQTableA(i, 0, newQValue);
                    }else{//B as the current QTable
                        double newQValue = getNewQValue(a, i, 0, a.getQTableB(), a.getQTableA());
                        a.setQTableB(i, 0, newQValue);
                    }
                }
            }//if the neighbor corresponding to the state i is eliminated, we jump to the next state to update Q-Value
        }
    }

    public void updateQTableAll() {
        for (int i = 0; i < N.RLAgentList.size(); i++) {
            int index = N.RLAgentList.get(i);
            AllAgentDQ allAgentDQ = N.agentsList.get(index);
            updateQTable(allAgentDQ);
        }
    }

    /**
     * get the new Q-Value in a state-action pair
     *
     * @param a    AgentQ
     * @param i    the state AgentQ a is in
     * @param coop action of the AgentQ, 0 if defector, 1 if cooperator
     * @return long value of the new Q-Value
     */
    private static double getNewQValue(AllAgentDQ a, int i, int coop, double[][] QTableC, double[][] QTableO ) {
        int nextState = (i + 1) % 4;//the next state
        double TD, maxFutureReward;
        int argmaxAction;
        if(QTableC[nextState][0] > QTableC[nextState][1]){
            argmaxAction = 0;
        }else{
            argmaxAction = 1;
        }
        maxFutureReward = QTableO[nextState][argmaxAction];
        TD = a.getRTable()[i][coop] + discountR * maxFutureReward - QTableC[i][coop];
        return (QTableC[i][coop] + learningR * TD);
    }

    /**
     * 1. If an alive agent's actualPayoffs < tParameter*normalPayoff, it is eliminated
     * 2. Also remove all edges between the agent and its neighbors if the agent is eliminated
     *
     * @param index index of the agent in Network.agentList
     * @return boolean of whether the agent is eliminated
     * @throws Exception
     */
    public boolean agentRemove(int index) throws Exception {
        AllAgentDQ a = N.agentsList.get(index);
        if (a.getActualPayoffs() < alpha * normalPayoff && !a.getEliminated()) {
            List<Integer> neighbors = a.getAdjLists(); //get all agents' neighbor's index
            for (Integer i : neighbors) {
                AllAgentDQ ai = N.agentsList.get(i);
                ai.getAdjLists().remove((Integer) index);  //remove all edges between agent and its neighbor
            }
            return true;
        }
        return false;
    }

    /**
     * 1. Apply agentRemove() to all agents in the network
     * 2. Eliminate all agents with no neighbor
     *
     * @throws Exception
     */
    public void agentRemoveAll() throws Exception {
        if (N.aliveAgentCount == 0) {
            return;
        }
        for (int i = 0; i < N.agentCount; i++) {
            AllAgentDQ a = N.agentsList.get(i);
            if (agentRemove(i)) {
                a.setEliminated(true);
                N.aliveAgentCount--;
                if (a.getCooperate()) {
                    N.cooperatorCount--;
                }
                if (a.isActivated()) {
                    N.RLAgentList.remove((Integer) i);
                    if (a.getClock() == -1) {
                        expiredClockCount--;
                    }//if the removed agent is RL agent whose clock has expired, update the number of expired clock
                }//if the removed agent is an RL agent, update RLAgentList
            }

        }

        for (int i = 0; i < N.agentCount; i++) {
            AllAgentDQ a = N.agentsList.get(i);
            if (a.neighborNum() == 0 && !a.getEliminated()) {
                a.setEliminated(true);
                N.aliveAgentCount--;
                if (a.getCooperate()) {
                    N.cooperatorCount--;
                }
                if (a.isActivated()) {
                    N.RLAgentList.remove((Integer) i);
                    if (a.getClock() == -1) {
                        expiredClockCount--;
                    }//if the removed agent is RL agent whose clock has expired, update the number of expired clock
                }//if the removed agent is an RL agent, update RLAgentList
            }
        }
    }


    public void updateRTable(AllAgentDQ a) {
        double[][] rTable = a.getRTable();
        for (int i = 0; i < rTable.length; i++) {
            AllAgentDQ neigh = a.getQNeighborList().get(i);
            if (!neigh.getEliminated()) {
                boolean coop = neigh.getCooperate();
                for (int j = 0; j < rTable[0].length; j++) {
                    if (j == 0) {  //if the agent a is defector
                        if (coop) {//if the neighbor is cooperator
                            a.setRTable(i, j, T);
                        } else {//if the neighbor is a defector
                            a.setQTableA(i, j, 0);
                        }
                    } else { //if the agent a is a cooperator
                        if (coop) {//if the neighbor is cooperator
                            a.setRTable(i, j, 1);
                        } else {//if the neighbor is a defector
                            a.setQTableA(i, j, 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * Update the R-Table for all RL agents for them to calculate Q-Value
     */
    public void updateRTableAll() {
        for (int i = 0; i < N.RLAgentList.size(); i++) {
            int index = N.RLAgentList.get(i);
            AllAgentDQ allAgentDQ = N.agentsList.get(index);
            updateRTable(allAgentDQ);
        }
    }


    /**
     * @param eliminateRecord is an ArrayList<Double> that stores percentage of eliminated agents in every round
     * @return a boolean of whether the network reaches a steady state
     */
    public boolean ifSteady(ArrayList<Double> eliminateRecord) throws Exception {
        //1. if all are dead
        //2. if all agents have become RL agents && their clock has expired (int trainingPeriod)
        //3. if all alive agents are RL agents, their clock expired, and the percentage of dead agents remain the same for 1000 trials
        int size = eliminateRecord.size();


        if ((eliminateRecord.get(size - 1).compareTo(1.0)) == 0) {
            return true;
        } else if (N.aliveAgentCount > N.RLAgentList.size()) {
            return false;
        } else if (N.aliveAgentCount == N.RLAgentList.size()) {
            if (N.RLAgentList.size() == expiredClockCount) {
                trialNum++;//only increment trialNum when all alive agents are RL && their clock has expired
                if (trialNum == 1001) {
                    return true;
                } else if (trialNum >= 2) {
                    if (eliminateRecord.get(size - 1).compareTo(eliminateRecord.get(size - 2)) != 0) {
                        trialNum = 0;//if the percentage of dead agents are different to the previous trial, reset trialNum
                        return false;//
                    }
                }
            }
        } else {
            throw new Exception("alive agents number is less than RL agent number: need to fix bug");
        }
        return false;
    }

    /**
     * Round a value to value of precision
     *
     * @param value     double
     * @param precision
     * @return a double
     */
    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public int totalPair(){
        int totalPair = 0;
        for(AllAgentDQ agentQ : N.agentsList){
            if(!agentQ.getEliminated()){
                totalPair += agentQ.neighborNum();
            }
        }
        return totalPair;
    }

    public double[] printCDCCDDPairPercent() {
        int CDcount = 0, CCcount = 0, DDcount = 0;
        double[] result = new double[4];
        for (int i = 0; i < N.agentCount; i++) {
            AllAgentDQ allAgentDQ = N.agentsList.get(i);
            if(!allAgentDQ.getEliminated()) {
                if (allAgentDQ.getCooperate()) {
                    for (Integer j : allAgentDQ.getAdjLists()) {
                        AllAgentDQ neighbor = N.agentsList.get(j);
                        if (!neighbor.getCooperate()) {
                            CDcount++;
                        } else { //if both agents cooperate
                            CCcount++;
                        }
                    }
                } else {
                    for (Integer j : allAgentDQ.getAdjLists()) {
                        AllAgentDQ neighbor = N.agentsList.get(j);
                        if (neighbor.getCooperate()) {
                            CDcount++;
                        } else { //if both agents don't cooperate
                            DDcount++;

                        }
                    }
                }
            }
        }
        int totalPair = totalPair();
        result[0] = (double) CDcount/totalPair;
        result[1] = (double) CCcount/totalPair;
        result[2] = (double) DDcount/totalPair;
        result[3] = totalPair;
        return result;
    }
}
