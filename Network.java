import java.io.IOException;
import java.util.*;
import java.io.FileWriter;
public class Network { //original: public class Network implements Iterable<Integer>

    //2D4n network always or change network later?
    //another program that generate network and output a text file of network
    //PDG program read info from the text file -> a more efficient approach
    //Good for different kinds of network
    //format of network can be read by software such as (network bench, pajek)
    public ArrayList<Agent> agentsList;
    public int agentCount; //final int -> int for agentRemove() in PDG 06/28

    public int cooperatorCount;

    public class Agent {

        public int index;
        public LinkedList<Agent> adjLists;
        public double actualPayoffs;//agent's actualPayoffs in one trial
        public boolean cooperate;

        public Agent(int index) {
            this.index = index;
            adjLists = new LinkedList<>();
            actualPayoffs = 0; //refresh before each trail
            cooperate = false;  //initialize agent as defector, will reset them in functions in PlayPDG 06/28
            //add initial num of neighbors, num of neighbors rn?

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

        /**
         * Returns a list of all the neighbors of agent v in the network
         */
        public LinkedList<Agent> getAdjLists() {
            return adjLists;
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
            boolean result;
            result = this.cooperate;

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

    public void strategyUpdateAll() {
        for (int i = 0; i < agentCount; i++) {

            Agent a = agentsList.get(i);
            boolean originalCoop = a.cooperate;
            a.setCooperate(a.strategyUpdate());
            if(a.getCooperate()==false && originalCoop==true){
                cooperatorCount--;
            } else if (a.getCooperate()==true && originalCoop==false) {
                cooperatorCount++;

            }


        }
    }

    /**
     * Initializes a network with numAgents and no Edges.
     *
     * @param numAgents       > 4
     * @param defectorPercent
     */
    public Network(int numAgents, double defectorPercent) {
        agentsList = new ArrayList<Agent>();


        ArrayList<Integer> number = new ArrayList<>();
        for (int k = 0; k < numAgents; k++) {
            number.add(k);
        }
        Collections.shuffle(number);

        for (int i = 0; i < numAgents; i++) {
            agentsList.add(new Agent(number.get(i)));
        }

        agentCount = numAgents;

        int numDefect = (int) Math.round(numAgents * defectorPercent);
        cooperatorCount = numAgents - numDefect;

        ArrayList<Boolean> ifCooperate = new ArrayList<>();

        for (int i = 0; i < numDefect; i++) {
            ifCooperate.add(false);
        }
        for (int i = 0; i < numAgents - numDefect; i++) {
            ifCooperate.add(true);
        }
        Collections.shuffle(ifCooperate);
        System.out.println("if cooperate content:" + ifCooperate);
        for (int i = 0; i < agentCount; i++) {
            agentsList.get(i).setCooperate(ifCooperate.get(i));
        }
    }

    /*public class Edge {

        private final int from;
        private final int to;



        Edge(int from, int to) { //deleted instance variable, weight, 06/28
            this.from = from;
            this.to = to;
        }

        public int getFrom(){
            return from;
        }

        public int getTo(){
            return to;
        }

        public String toString() {
            return "(" + from + ", " + to + ")";
        }

    }*/


    /**
     * Adds an edge (V1, V2) between two neighbors in the network.
     */
    //If the Edge already exists, replaces the current Edge with a new Edge with. should we write about this case?
    //06/28
    public void addEdge(Agent a1, Agent a2) {
        if (a1.adjLists.contains(a2)) {
            return;
        }
        a1.adjLists.add(a2);
        a2.adjLists.add(a1);
    }


    /**
     * Returns true if there exists an Edge from agent FROM to agent TO.
     * Returns false otherwise.
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


    //generate a 2D4N network with agentNum number of agents
    public void generate2D4N() {
        for (int i = 0; i < agentCount; i++) {
            LinkedList<Agent> neighbors = agentsList.get(i).adjLists;
            addEdge(agentsList.get(i), agentsList.get((i + 1) % agentCount));
            addEdge(agentsList.get(i), agentsList.get((i + 2) % agentCount));
            addEdge(agentsList.get(i), agentsList.get((i - 1 + agentCount) % agentCount));
            addEdge(agentsList.get(i), agentsList.get((i - 2 + agentCount) % agentCount));
        }
    }

    public void printNetworkToFile() throws IOException {
        FileWriter Network2D4N = new FileWriter("2D4N.txt");

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
        N.printNetworkToFile();
    }
}




    /** Returns an Iterator that outputs the agents of the graph in topological
       sorted order. */
    /*
    public Iterator<Integer> iterator() {
        return new TopologicalIterator();
    }

     */


    /* Returns the collected result of performing a depth-first search on this
       network's agents starting from V. */
    /*
    public List<Integer> dfs(int v) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new DFSIterator(v);

        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

     */

    /* Returns true iff there exists a path from START to STOP. Assumes both
       START and STOP are in this network. If START == STOP, returns true. */
    /*
    public boolean pathExists(int start, int stop) {
        return dfs(start).contains(stop);
    }

     */


    /* Returns the path from START to STOP. If no path exists, returns an empty
       List. If START == STOP, returns a List with START. */
    /*
    public List<Integer> path(int start, int stop) {
        List<Integer> result = new ArrayList<Integer>();
        if (start == stop){
            result.add(start);
        }else if (pathExists(start, stop)) {
            List<Integer> path = dfs(start);
            int i = path.indexOf(stop);
            int pathNode= stop;
            result.add(stop);
            while (path.get(i) != start) {
                if (isAdjacent(pathNode, path.get(i-1))||isAdjacent(path.get(i-1), pathNode)){
                    result.add(path.get(i-1));
                    pathNode = path.get(i-1);
                }
                i--;
            }
        }
        List<Integer> temp = new ArrayList<>();
        for (int i = result.size() -1; i >= 0; i--){
            temp.add(result.get(i));
        }
        return temp;
    }

     */

    /*
    public List<Integer> topologicalSort() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new TopologicalIterator();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

     */


    /*
    private void printDFS(int start) {
        System.out.println("DFS traversal starting at " + start);
        List<Integer> result = dfs(start);
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
        System.out.println();
        System.out.println();
    }

    private void printPath(int start, int end) {
        System.out.println("Path from " + start + " to " + end);
        List<Integer> result = path(start, end);
        if (result.size() == 0) {
            System.out.println("No path from " + start + " to " + end);
            return;
        }
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
        System.out.println();
        System.out.println();
    }

}*/
