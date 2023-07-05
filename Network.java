import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Network {
    /** agentList stores an ArrayList of Agent object*/
    public ArrayList<Agent> agentsList;
    public final int agentCount;//number of total agents in the network, alive or eliminated
    public int aliveAgentCount;//number of alive agents in the network
    public int cooperatorCount;//number of alive cooperators in the network
    //we need to calculate eliminatedPercent and defectorPercent after each trial using the above instance variables

    /**
     * 1. Initializes a network with numAgents and no edges.
     * 2. Randomly place defectorPercent percentage of defectors in the network
     * 3. Initialize the rest of the agents as cooperators
     * 4. Update aliveAgentCount and cooperatorCount
     * @param numAgents > 4
     * @param defectorPercent percentage of defector in the network
     */
    public Network(int numAgents, double defectorPercent) {
        agentsList = new ArrayList<Agent>();
        agentCount = numAgents;
        aliveAgentCount = numAgents;

        for (int i = 0; i < numAgents; i++) {
            agentsList.add(new Agent(i));
        }

        int numDefect = (int) Math.round(numAgents * defectorPercent);
        cooperatorCount = numAgents - numDefect;

        ArrayList<Boolean> ifCooperate = new ArrayList<>();

        for (int i = 0; i < numDefect; i++) {
            ifCooperate.add(false);
        }

        for (int i = 0; i < numAgents - numDefect; i++) {
            ifCooperate.add(true);
        }

        Collections.shuffle(ifCooperate); //shuffle the sequence of true and false
        //System.out.println("if cooperate content:" + ifCooperate);
        for (int i = 0; i < agentCount; i++) {
            agentsList.get(i).setCooperate(ifCooperate.get(i));
        }//update all agents' cooperate instance variable according to ifCooperate ArrayList
    }


    /**
     * 1. Add an undirected edge between agent with index a1 and a2 by updating their respective adjList
     * 2. If a1 == a2, or the edge already exist, do nothing
     * @param a1 index of one agent
     * @param a2 index of another agent
     */
    public void addEdge(int a1, int a2) {
        Agent agent1 = agentsList.get(a1);
        Agent agent2 = agentsList.get(a2);
        if (agent1.adjLists.contains(a2) || a1 == a2){
            return;
        }
        agent1.adjLists.add(a2);//add a2 to agent1's adjList
        agent2.adjLists.add(a1);//add a1 to agent2's adjList
    }


    /**
     * 1. Check if two agents are adjacent
     * 2. If a2 = a2, or one of the agent is already eliminated, return false
     * @param a1 index of one agent
     * @param a2 index of another agent
     * @return a boolean of whether a1 and a2 are adjacent
     */
    public boolean isAdjacent(int a1, int a2) {
        if(agentsList.get(a1).getEliminated() || agentsList.get(a2).getEliminated()) return false;
        if (a1 == a2) {
            return false;
        }
        Agent a = agentsList.get(a1);
        for (Integer i :  a.adjLists) {
            if (i == a2){
                return true;
            }
        }
        return  false;
    }

    /**
     * Generate a 2D4N network (4-regular graph) with agentNum number of agents
     */
    public void generate2D4N() {
        for (int i = 0; i < agentCount; i++) {
            addEdge(i, (i + 1) % agentCount);
            addEdge(i, (i + 2) % agentCount);
            addEdge(i, (i - 1 + agentCount) % agentCount);
            addEdge(i, (i - 2 + agentCount) % agentCount);
            //System.out.println((i - 2 + agentCount) % agentCount);
        }
    }

    /**
     * Print the given network to a file with filename in the format of Pajek net file
     * @param filename String of file name
     * @throws IOException if the file cannot be opened or closed
     */
    public void printNetworkToFile(String filename) throws IOException {
        FileWriter trialOutput = new FileWriter(filename);//open file for writing
        trialOutput.write("*Vertices "+agentCount + "\n");
        for (int i = 0; i < agentCount; i++){
            int printValue = i + 1;
            trialOutput.write(printValue + " " + "\"" + printValue + "\" ic ");
            Agent a = agentsList.get(i);
            if (a.getEliminated()){
                trialOutput.write("Gray\n");
            }else{
                if (a.getCooperate()){
                    trialOutput.write("Blue\n");
                }else{
                    trialOutput.write("Black\n");
                }
            }
        }//write all vertices in the network to file

        trialOutput.write("*Edges\n");
        for (int i = 0; i < agentCount; i++){
            int printValue1 = i + 1;
            Agent a = agentsList.get(i);
            if (!a.getEliminated()){
                for (int j = 0; j < a.neighborNum(); j++){
                    int printValue2 = a.getAdjLists().get(j) + 1;
                    trialOutput.write(printValue1 + " " + printValue2 + "\n");
                }
            }
        }//write all edges in the network to file

        System.out.println("Successful");//print "Successful" if writing is successful
        trialOutput.close();
    }

    /**
     * 1. Print the given network to standard output in adjacency matrix
     * 2. Also prints booleans of if an agent is eliminated, or is cooperator
     */
    public void printNetwork(){
        for(int i = 0; i < agentCount; i++){
            System.out.print(agentsList.get(i).getCooperate() + " ");
        }
        System.out.print("\n");

        for(int i = 0; i < agentCount; i++){
            System.out.print(agentsList.get(i).getEliminated() + " ");
        }
        System.out.print("\n");

        for(int i = 0; i < agentCount; i++){
            Agent a = agentsList.get(i);
            for(int j = 0; j < agentCount; j++) {
                if(isAdjacent(i,j)){
                    System.out.print("1 ");
                }else{
                    System.out.print("0 ");
                }
            }
            System.out.print("\n");
        }
    }


    public static void main(String[] args) throws IOException {
        Network N = new Network(10, 0.2);
        N.generate2D4N();
        N.printNetwork();
        //N.printNetworkToFile("test.txt");
    }


    /**
     * Agent class where each Agent is an Agent in the network
     */
    public class Agent {
        /** Index of the agent in Network.agentList*/
        private int index;

        /** LinkedList<Integer> stores all indices of neighbors of an Agent*/
        public LinkedList<Integer> adjLists;

        /** Agent's actualPayoffs in one trial*/
        private double actualPayoffs;

        /** boolean of whether an agent is a cooperator: true if cooperator, false if defector*/
        private boolean cooperate;
        private boolean eliminated;

        /**
         * Agent constructor to construct an agent with a given index
         * @param index index of agent in Network.agentList
         */
        public Agent(int index) {
            this.index = index;
            adjLists = new LinkedList<>();
            actualPayoffs = 0;//reset to 0 before each trail
            cooperate = false; //initialize agent as defector, will reset them in functions in PlayPDG
            eliminated = false;//initialize the agent as not eliminated
        }

        /**
         * Getter method of instance variable index
         * @return index of an agent
         */
        public int getIndex() {
            return this.index;
        }

        /**
         * Setter method of instance variable index
         * @param index index we want to set for an agent
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * Getter method of instance variable actualPayoffs
         * @return actualPayoffs
         */
        public double getActualPayoffs(){
            return actualPayoffs;
        }

        /**
         * Setter method of instance variable actualPayoffs
         * @param actualPayoffs
         */
        public void setActualPayoffs(double actualPayoffs){
            this.actualPayoffs = actualPayoffs;
        }

        /**
         * Getter method of instance variable cooperate
         * @return cooperate
         */
        public boolean getCooperate() {
            return this.cooperate;
        }

        /**
         * Setter method of instance variable cooperate
         * @param cooperate
         */
        public void setCooperate(boolean cooperate) {
            this.cooperate = cooperate;
        }

        /**
         * Getter method of instance variable eliminated
         * @return eliminated
         */
        public boolean getEliminated (){
            return this.eliminated;
        }

        /**
         * Setter method of instance variable eliminated
         * @param eliminated
         */
        public void setEliminated(boolean eliminated){
            this.eliminated = eliminated;
        }

        /**
         * Getter method of adjList
         * @return adjList
         */
        public LinkedList<Integer> getAdjLists() {
            return adjLists;
        }

        /**
         * Returns the number of neighbors of an agent
         * @return int of number of neighbors
         */
        public int neighborNum(){
            return adjLists.size();
        }

        //we can write strategy update for reinforcement learning function later.
        //also update the q table in each trail
        //write beginning of code signify what learning approach will be used
        //
    }
}


