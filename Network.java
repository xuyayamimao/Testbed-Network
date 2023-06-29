import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Stack;
import java.util.HashSet;

public class Network{ //original: public class Network implements Iterable<Integer>

    //2D4n network always or change network later?
    //another program that generate network and output a text file of network
    //PDG program read info from the text file -> a more efficient approach
    //Good for different kinds of network
    //format of network can be read by software such as (network bench, pajek)
    public ArrayList<Agent> agentsList;
    public int agentCount; //final int -> int for agentRemove() in PDG 06/28

    public class Agent {
        public LinkedList<Edge> adjLists;
        public boolean hasPlayed; //do we still need this?
        public double actualPayoffs;//agent's actualPayoffs in one trial
        public boolean cooperate;
        public boolean eliminated; //Do we still need this instance variable? 06/28

        public Agent(){
            adjLists = new LinkedList<>();
            hasPlayed = false;
            actualPayoffs = 0; //refresh before each trail
            cooperate = false;  //initialize agent as defector, will reset them in functions in PlayPDG 06/28
            eliminated = false;
            //add initial num of neighbors, num of neighbors rn?

        }


        public boolean getCooperate(){
            return this.cooperate;
        }

        public void setCooperate(boolean cooperate){
            this.cooperate = cooperate;
        }



        /**
         * Function enable an agent to update its strategy
         * Randomly choose a survived neighbor to imitate and update strategy
         * @Return a boolean that mark the new strategy of the agent for the next trail
         */
        public boolean strategyUpdate(){
            boolean result;
            result = this.cooperate;
            int neighborNum = adjLists.size();
            int imiIndex = (int)Math.random()*neighborNum;//index of a randomly chosen neighbor
            int imiNeighborIndex = adjLists.get(imiIndex).to;
            double noise = 0.1;//constant value of uncertainty in assessing payoff
            Agent imiNeighbor = agentsList.get(imiNeighborIndex);
            double Wij = 1/(1+Math.exp(-(imiNeighbor.actualPayoffs - this.actualPayoffs)/noise));
            if (Math.random() < Wij){
                result = imiNeighbor.getCooperate();
            }
            return result;

        }

        //we can write strategy update for reinforcement learning function later.
        //-also update the q table in each trail
        //write beginning of code signify what learning approach will be used
        //


    }
    public void strategyUpdateAll(){
        ArrayList<Boolean> totalResult = new ArrayList<>();
        for(int i=0; i<agentsList.size(); i++){
            totalResult.add(agentsList.get(i).strategyUpdate());
        }
        for(int i=0; i<agentsList.size(); i++){
            agentsList.get(i).setCooperate(totalResult.get(i));
        }

    }

    /** Initializes a network with numAgents and no Edges. */
    public Network(int numAgents) {
        agentsList = (ArrayList<Agent>) new ArrayList<Agent>();
        for (int k = 0; k < numAgents; k++) {
            agentsList.add(k, new Agent());
        }
        agentCount = numAgents;
    }

    public class Edge {

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

    }


    /**
     *
     * Adds an edge (V1, V2) between two neighbors in the network.
     *
     */
    //If the Edge already exists, replaces the current Edge with a new Edge with. should we write about this case?
    //06/28
    public void addEdge(int v1, int v2) {
        LinkedList<Edge> v1neighbors = agentsList.get(v1).adjLists;
        LinkedList<Edge> v2neighbors = agentsList.get(v2).adjLists;
        v1neighbors.add(new Edge(v1, v2));
        v2neighbors.add(new Edge(v2, v1));
    }




    /** Returns true if there exists an Edge from agent FROM to agent TO.
       Returns false otherwise. */
    public boolean isAdjacent(int from, int to) {
        if (from == to){
            return  false;
        }
        for (Edge e: agentsList.get(from).adjLists){
            if (e. from == from && e.to == to){
                return true;
            }
        }
        return false;
    }

    /** Returns a list of all the neighbors of agent v in the network */
    public List<Integer> neighbors(int v) {
        List<Integer> neighbors = new ArrayList<>();
        for (Edge e: agentsList.get(v).adjLists){
            neighbors.add(e.to);
        }
        return neighbors;
    }

    //generate a 2D4N network with agentNum number of agents
    public void generate2D4N(){
        for (int i = 0; i < agentCount; i++){
            LinkedList<Edge> neighbors = agentsList.get(i).adjLists;
            addEdge(i, (i+1)%agentCount);
            Edge edgeA = new Edge(i,(i+1)%agentCount); //added 06/28
            neighbors.add(edgeA);
            addEdge(i, (i+2)%agentCount);
            Edge edgeB = new Edge(i,(i+2)%agentCount); //added 06/28
            neighbors.add(edgeB);
            addEdge(i, (i-1+agentCount)%agentCount);
            Edge edgeC = new Edge(i,(i-1+agentCount)%agentCount); //added 06/28
            neighbors.add(edgeC);
            addEdge(i, (i-2+agentCount)%agentCount);
            Edge edgeD = new Edge(i,(i-2+agentCount)%agentCount); //added 06/28
            neighbors.add(edgeD);
        }
    }

    public void printNetwork(){
        int[][] adjMatrix = new int[agentCount][agentCount];
        System.out.print("  ");
        for(int i=0; i<agentCount; i++){
            System.out.print(i + " ");
        }
        System.out.print("\n");
        for (int i = 0; i < agentCount; i++){
            System.out.print(i+" ");
            for (int j = 0; j < agentCount; j++){
                if (isAdjacent(i, j)){
                    adjMatrix[i][j] = 1;
                }else{
                    adjMatrix[i][j] = 0;
                }

                System.out.print(adjMatrix[i][j]+" ");
            }
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
        Network N = new Network( 100);
        N.generate2D4N();
        N.printNetwork();





    }




    /** Returns an Iterator that outputs the agents of the graph in topological
       sorted order. */
    /*
    public Iterator<Integer> iterator() {
        return new TopologicalIterator();
    }

     */

    /**
     *  A class that iterates through the agents of this graph,
     *  starting with a given agent. Does not necessarily iterate
     *  through all agents in the graph: if the iteration starts
     *  at a agent v, and there is no path from v to an agent w,
     *  then the iteration will not include w.
     */

    /*
    private class DFSIterator implements Iterator<Integer> {

        private final Stack<Integer> fringe;
        private final HashSet<Integer> visited;

        public DFSIterator(Integer start) {
            fringe = new Stack<>();
            visited = new HashSet<>();
            fringe.push(start);
        }

        public boolean hasNext() {
            if (!fringe.isEmpty()) {
                int i = fringe.pop();
                while (visited.contains(i)) {
                    if (fringe.isEmpty()) {
                        return false;
                    }
                    i = fringe.pop();
                }
                fringe.push(i);
                return true;
            }
            return false;
        }

        public Integer next() {
            int curr = fringe.pop();
            ArrayList<Integer> lst = new ArrayList<>();
            for (int i : neighbors(curr)) {
                lst.add(i);
            }
            lst.sort((Integer i1, Integer i2) -> -(i1 - i2));
            for (Integer e : lst) {
                fringe.push(e);
            }
            visited.add(curr);
            return curr;
        }

        //ignore this method
        public void remove() {
            throw new UnsupportedOperationException(
                    "vertex removal not implemented");
        }

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
    private class TopologicalIterator implements Iterator<Integer> {

        private final Stack<Integer> fringe;

        private final int[] currentInDegree;

        TopologicalIterator() {
            fringe = new Stack<Integer>();
            currentInDegree = new int[agentCount];
            for (int i = 0; i < agentCount; i++){
                if(inDegree(i) == 0){
                    fringe.push(i);
                    currentInDegree[i]= -1;
                }else{
                    currentInDegree[i] = inDegree(i);
                }
            }
        }


        public boolean hasNext() {
            return !fringe.isEmpty();
        }

        public Integer next() {
            int curr = fringe.pop();
            for (int i = 0; i < agentCount; i++){
                if (isAdjacent(curr, i)){
                    currentInDegree[i] -= 1;
                }
                if (currentInDegree[i] == 0){
                    fringe.push(i);
                    currentInDegree[i] = -1;
                }
            }

            return curr;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

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

    private void printTopologicalSort() {
        System.out.println("Topological sort");
        List<Integer> result = topologicalSort();
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
    }

     */


}
