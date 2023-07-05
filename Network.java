import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Network { //original: public class Network implements Iterable<Integer>
    //another program that generate network and output a text file of network
    //PDG program read info from the text file -> a more efficient approach
    //Good for different kinds of network
    //format of network can be read by software such as (network bench, pajek)
    public ArrayList<Agent> agentsList;
    public int agentCount; //final int -> int for agentRemove() in PDG 06/28
    public int aliveAgentCount;
    public int cooperatorCount;
    //we need to calculate eliminatedPercent and defectorPercent after each trial using the above instance variables

    /**
     * Initializes a network with numAgents and no Edges.
     *
     * @param numAgents > 4
     * @param defectorPercent
     */
    public Network(int numAgents, double defectorPercent) {
        agentsList = new ArrayList<Agent>();
        for (int i = 0; i < numAgents; i++) {
            agentsList.add(new Agent(i));
        }

        agentCount = numAgents;
        aliveAgentCount = numAgents;

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
        System.out.println("if cooperate content:" + ifCooperate);
        for (int i = 0; i < agentCount; i++) {
            agentsList.get(i).setCooperate(ifCooperate.get(i));
        }
    }


    /**
     * Adds an edge (V1, V2) between two neighbors in the network.
     * If the edge (V1, V2) already exist, return
     */
    public void addEdge(int a1, int a2) {
        Agent agent1 = agentsList.get(a1);
        Agent agent2 = agentsList.get(a2);
        if (agent1.adjLists.contains(a2) || a1 == a2) {
            return;
        }
        agent1.adjLists.add(a2);
        agent2.adjLists.add(a1);
    }


    /**
     * returns true if there exists an Edge from agent FROM to agent TO.
     * returns false otherwise.
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
     * generate a 2D4N network with agentNum number of agents
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
     * print the given network to a file with filename in the format of Pajek net file
     * @throws IOException if the file cannot be opened
     */
    public void printNetworkToFile(String filename) throws IOException {
        FileWriter trialOutput = new FileWriter(filename);
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
        }
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
        }
        System.out.println("Successful");
        trialOutput.close();
    }

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


    public class Agent {

        private int index;
        public LinkedList<Integer> adjLists;
        private double actualPayoffs;//agent's actualPayoffs in one trial
        private boolean cooperate;
        private boolean eliminated;

        public Agent(int index) {
            this.index = index;
            adjLists = new LinkedList<>();
            actualPayoffs = 0; //refresh before each trail
            cooperate = false;  //initialize agent as defector, will reset them in functions in PlayPDG 06/28
            eliminated = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getActualPayoffs(){
            return actualPayoffs;
        }

        public void setActualPayoffs(double actualPayoffs){
            this.actualPayoffs = actualPayoffs;
        }

        public boolean getCooperate() {
            return this.cooperate;
        }

        public void setCooperate(boolean cooperate) {
            this.cooperate = cooperate;
        }

        public boolean getEliminated (){
            return this.eliminated;
        }

        public void setEliminated(boolean eliminated){
            this.eliminated = eliminated;
        }

        /**
         * Returns a list of all the neighbors of agent v in the network
         */
        public LinkedList<Integer> getAdjLists() {
            return adjLists;
        }

        public int neighborNum(){
            return adjLists.size();
        }

        @Override
        public boolean equals(Object a) {
            if (this == a) {
                return true;
            }
            if (a.getClass() != this.getClass()) {
                return false;
            }
            return this.index == ((Agent) a).index;
        }



        //we can write strategy update for reinforcement learning function later.
        //-also update the q table in each trail
        //write beginning of code signify what learning approach will be used
        //


    }
}


