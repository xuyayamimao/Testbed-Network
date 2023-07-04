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

    public void strategyUpdateAll() {
        for (int i = 0; i < agentCount; i++) {
            Agent a = agentsList.get(i);
            boolean originalCoop = a.cooperate;
            a.setCooperate(a.strategyUpdate());
            if (a.getCooperate() == false && originalCoop == true) {
                cooperatorCount--;
            } else if (a.getCooperate() == true && originalCoop == false) {
                cooperatorCount++;
            }
        }
    }

    /**
     * Adds an edge (V1, V2) between two neighbors in the network.
     * If the edge (V1, V2) already exist, return
     */
    public void addEdge(Agent a1, Agent a2) {
        if (a1.adjLists.contains(a2)) {
            return;
        }
        a1.adjLists.add(a2);
        a2.adjLists.add(a1);
    }


    /**
     * returns true if there exists an Edge from agent FROM to agent TO.
     * returns false otherwise.
     */
    public boolean isAdjacent(Agent a1, Agent a2) {
        if (a1.equals(a2)) {
            return false;
        }
        for (Agent a : a1.adjLists) {
            if (a.equals(a2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * generate a 2D4N network with agentNum number of agents
     */
    public void generate2D4N() {
        for (int i = 0; i < agentCount; i++) {
            LinkedList<Agent> neighbors = agentsList.get(i).adjLists;
            addEdge(agentsList.get(i), agentsList.get((i + 1) % agentCount));
            addEdge(agentsList.get(i), agentsList.get((i + 2) % agentCount));
            addEdge(agentsList.get(i), agentsList.get((i - 1 + agentCount) % agentCount));
            addEdge(agentsList.get(i), agentsList.get((i - 2 + agentCount) % agentCount));
        }
    }

    /**
     * print the given network to a file with filename in the format of Pajek net file
     * @throws IOException if the file cannot be opened
     */
    public void printNetworkToFile(String filename) throws IOException {
        FileWriter Network2D4N = new FileWriter(filename);
        int[][] adjMatrix = new int[agentCount][agentCount];
        Network2D4N.write(" ");
        for (int i = 0; i < agentCount; i++) {
            Network2D4N.write(agentsList.get(i).getIndex() + " ");
        }
        Network2D4N.write("\n");
        for (int i = 0; i < agentCount; i++) {
            Network2D4N.write(agentsList.get(i).getIndex() + " ");
            for (int j = 0; j < agentCount; j++) {
                if (isAdjacent(agentsList.get(i), agentsList.get(j))) {
                    adjMatrix[i][j] = 1;
                } else {
                    adjMatrix[i][j] = 0;
                }
                Network2D4N.write(adjMatrix[i][j] + " ");
            }
            Network2D4N.write("\n");
        }
        Network2D4N.close();
    }

    public static void main(String[] args) throws IOException {
        Network N = new Network(10000, 0.2);
        N.generate2D4N();
        N.printNetworkToFile("2D4N.txt");
    }





    public class Agent {

        public int index;
        public LinkedList<Agent> adjLists;
        public double actualPayoffs;//agent's actualPayoffs in one trial
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
        public LinkedList<Agent> getAdjLists() {
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

        /**
         * Function enable an agent to update its strategy
         * Randomly choose a survived neighbor to imitate and update strategy
         *
         * @Return a boolean that mark the new strategy of the agent for the next trail
         */
        public boolean strategyUpdate() {
            boolean result = this.cooperate;
            int neighborNum = adjLists.size();
            int imiIndex = (int) Math.random() * neighborNum;//index of a randomly chosen neighbor
            Agent imiNeighbor = adjLists.get(imiIndex);
            double noise = 0.1;//constant value of uncertainty in assessing payoff
            double Wij = 1 / (1 + Math.exp(-(imiNeighbor.actualPayoffs - this.actualPayoffs) / noise));
            if (Math.random() < Wij) {
                result = imiNeighbor.getCooperate();
            }
            return result;
        }

        //we can write strategy update for reinforcement learning function later.
        //-also update the q table in each trail
        //write beginning of code signify what learning approach will be used
        //


    }
}


