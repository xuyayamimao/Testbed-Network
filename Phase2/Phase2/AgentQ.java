package Phase2;

import java.util.*;

/**
 * Agent class where each Agent is an Agent in the network
 */
public class AgentQ {
    /**
     * Index of the agent in Network.agentList
     */
    private int index;

    /**
     * LinkedList<Integer> stores all indices of neighbors of an Agent
     */
    private LinkedList<Integer> adjLists;

    /**
     * Agent's actualPayoffs in one trial
     */
    private double actualPayoffs;

    /**
     * boolean of whether an agent is a cooperator: true if cooperator, false if defector
     */
    private boolean cooperate;
    private boolean eliminated;
    public int clock;//within trainingPeriod iterations, exploration is allowed

    /**
     * boolean of whether an agent is activated as a RL agent
     */
    private boolean activated;

    private long[][] QTable;

    private double[][] RTable;

    private int prevState;

    private List<AgentQ> QNeighborList;//the list of an AgentQ's four neighbors, the AgentQs in the list don't change

    /**
     * Agent constructor to construct an agent with a given index
     *
     * @param index index of agent in Network.agentList
     */
    public AgentQ(int index) {
        this.index = index;
        adjLists = new LinkedList<>();
        actualPayoffs = 0;//reset to 0 before each trail
        cooperate = true; //initialize agent as defector, will reset them in functions in PlayPDG
        eliminated = false;//initialize the agent as not eliminated
        activated = false;//initialize the agent as not RL agent
        QTable = null;
        RTable = null;
        prevState = -1;
        QNeighborList = new ArrayList<>();
    }

    /**
     * Getter method of instance variable index
     *
     * @return index of an agent
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Setter method of instance variable index
     *
     * @param index index we want to set for an agent
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Getter method of instance variable actualPayoffs
     *
     * @return actualPayoffs
     */
    public double getActualPayoffs() {
        return actualPayoffs;
    }

    /**
     * Setter method of instance variable actualPayoffs
     *
     * @param actualPayoffs
     */
    public void setActualPayoffs(double actualPayoffs) {
        this.actualPayoffs = actualPayoffs;
    }

    /**
     * Getter method of instance variable cooperate
     *
     * @return cooperate
     */
    public boolean getCooperate() {
        return this.cooperate;
    }

    /**
     * Setter method of instance variable cooperate
     *
     * @param cooperate
     */
    public void setCooperate(boolean cooperate) {
        this.cooperate = cooperate;
    }

    /**
     * Getter method of instance variable eliminated
     *
     * @return eliminated
     */
    public boolean getEliminated() {
        return this.eliminated;
    }

    /**
     * Setter method of instance variable eliminated
     *
     * @param eliminated
     */
    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }

    /**
     * Getter method of adjList
     *
     * @return adjList
     */
    public LinkedList<Integer> getAdjLists() {
        return adjLists;
    }

    /**
     * Returns whether an agent is an RL agent
     *
     * @return a boolean
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Activate an agent as RL agent
     */
    public void activate() {
        activated = true;
        QTable = new long[4][2];
        RTable = new double[4][2];
        Random action = new Random();
        setCooperate(action.nextBoolean());//randomly choose an action when an agent is first activated
        clock = 0;//initialize the clock to be 0
        prevState = -1;//initialize prevState to be -1 because there isn't a prevState yet
    }

    public long[][] getQTable() {
        return QTable;
    }

    public void setQTable(int row, int col, long val) {
        QTable[row][col] = val;
    }

    public double[][] getRTable() {
        return RTable;
    }

    public void setRTable(int  row, int col, double val) {
        RTable[row][col] = val;
    }

    public int getPrevState(){
        return prevState;
    }

    public void setPrevState(int i){
        prevState = i;
    }

    public List<AgentQ> getQNeighborList(){
        return QNeighborList;
    }

    public void addToQNeighborList(AgentQ a){
        QNeighborList.add(a);
    }

    public int getClock(){
        return clock;
    }

    public int incrementClock(){
        clock++;
        return clock;
    }

    public void expireClock(){
        clock = -1;
    }

    /**
     * Returns the number of neighbors of an agent
     *
     * @return int of number of neighbors
     */
    public int neighborNum() {
        return adjLists.size();
    }


    //we can write strategy update for reinforcement learning function later.
    //also update the q table in each trail
    //write beginning of code signify what learning approach will be used
    //
}
