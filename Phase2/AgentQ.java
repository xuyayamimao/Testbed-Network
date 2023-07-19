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

    /**
     * boolean of whether an agent is activated as a RL agent
     */
    private boolean activated;

    private double[][] QTable;

    private double[][] RTable;

    private int prevState;
    private int prevAction;

    private Set<Integer> aliveNeighbor;

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
        prevAction = -1;
        aliveNeighbor = new HashSet<>();
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
        QTable = new double[4][2];
        RTable = new double[4][2];
        Random action = new Random();
        setCooperate(action.nextBoolean());//randomly choose an action when an agent is first activated
        prevAction = 0;
        prevState = 0;
    }

    public double[][] getQTable() {
        return QTable;
    }

    public void setQTable(int row, int col, double val) {
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

    public int getPrevAction(){
        return prevAction;
    }

    public void setPrevAction(int i){
        prevAction = i;
    }

    public Set<Integer> getAliveNeighbor(){ //contains each neighbor's row index in the Q table
        return aliveNeighbor;
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
