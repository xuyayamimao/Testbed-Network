import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Stack;
import java.util.HashSet;

public class Network implements Iterable<Integer>{

    public ArrayList<Agent> agentsList;
    public final int agentCount;

    public class Agent {
        public LinkedList<Edge> adjLists;
        public boolean hasPlayed;
        public int actualPayoffs;//agent's actualPayoffs in one trial
        public boolean cooperate;
        public boolean eliminated;

        public Agent(){
            adjLists = new LinkedList<>();
            hasPlayed = false;
            actualPayoffs = 0;
            cooperate = false;
            eliminated = false;
        }


        public boolean getCooperate(){
            return this.cooperate;
        }

        public void setCooperate(boolean cooperate){
            this.cooperate = cooperate;
        }

        /**Strategy Update
         * Randomly choose a survived neighbor to imitate and update strategy
         */
        public void stratgyUpdate(){
            boolean result;
            int neighborNum = adjLists.size();
            int imiIndex = (int)Math.random()*neighborNum;//index of a randomly chosen neighbor
            int imiNeighborIndex = adjLists.get(imiIndex).to;
            double noise = 0.1;//constant value of uncertainty in assessing payoff
            Agent imiNeighbor = agentsList.get(imiNeighborIndex);
            double Wij = 1/(1+Math.exp(-(imiNeighbor.actualPayoffs - this.actualPayoffs)/noise));
            if (Math.random() < Wij){
                result = imiNeighbor.getCooperate();
                setCooperate(result);
            }else{
                return;
            }
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


    /** Adds a directed Edge (V1, V2) to the network. That is, adds an edge
       in ONE direction, from agent v1 to agent v2. */
    public void addEdge(int v1, int v2) {
        addEdge(v1, v2, 0);
    }

    /* Adds an undirected Edge (V1, V2) to the graph. That is, adds an edge
       in BOTH directions, from v1 to v2 and from v2 to v1. */
    public void addUndirectedEdge(int v1, int v2) {
        addUndirectedEdge(v1, v2, 0);
    }

    /** Adds a directed Edge (V1, V2) to the network with weight WEIGHT. If the
       Edge already exists, replaces the current Edge with a new Edge with
       weight WEIGHT. */
    public void addEdge(int v1, int v2, int weight) {
        LinkedList<Edge> v1neighbors = agentsList.get(v1).adjLists;
        for (Edge e: v1neighbors){
            if (e.from == v1 && e.to == v2){
                e.weight = weight;
                return;
            }
        }
        v1neighbors.add(new Edge(v1, v2, weight));
    }

    /** Adds an undirected Edge (V1, V2) to the network with weight WEIGHT. If the
       Edge already exists, replaces the current Edge with a new Edge with
       weight WEIGHT. */
    public void addUndirectedEdge(int v1, int v2, int weight) {
        addEdge(v1, v2, weight);
        addEdge(v2, v1, weight);
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

    /** Returns the number of incoming Edges for agent V. */
    public int inDegree(int v) {
        int sum  = 0;
        for (int i = 0; i < agentCount; i++){
            if(i!=v){
                for (Edge e: agentsList.get(i).adjLists){
                    if (e.to == v){
                        sum++;
                    }
                }
            }
        }
        return sum;
    }//we probably won't need this method because inDegree only exists for directed edge

    /** Returns an Iterator that outputs the agents of the graph in topological
       sorted order. */
    public Iterator<Integer> iterator() {
        return new TopologicalIterator();
    }

    /**
     *  A class that iterates through the agents of this graph,
     *  starting with a given agent. Does not necessarily iterate
     *  through all agents in the graph: if the iteration starts
     *  at a agent v, and there is no path from v to an agent w,
     *  then the iteration will not include w.
     */
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

    /* Returns the collected result of performing a depth-first search on this
       network's agents starting from V. */
    public List<Integer> dfs(int v) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new DFSIterator(v);

        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    /* Returns true iff there exists a path from START to STOP. Assumes both
       START and STOP are in this network. If START == STOP, returns true. */
    public boolean pathExists(int start, int stop) {
        return dfs(start).contains(stop);
    }


    /* Returns the path from START to STOP. If no path exists, returns an empty
       List. If START == STOP, returns a List with START. */
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

    public List<Integer> topologicalSort() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new TopologicalIterator();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

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

    private class Edge {

        private final int from;
        private final int to;
        private int weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public String toString() {
            return "(" + from + ", " + to + ", weight = " + weight + ")";
        }

    }

    private void generateN1() {
        addUndirectedEdge(0, 1);
        addUndirectedEdge(0, 2);
        addUndirectedEdge(0, 4);
        addUndirectedEdge(1, 2);
        addUndirectedEdge(2, 0);
        addUndirectedEdge(2, 3);
        addUndirectedEdge(4, 3);
    }

    private void generateN2() {
        addUndirectedEdge(0, 1);
        addUndirectedEdge(0, 2);
        addUndirectedEdge(0, 3);
        addUndirectedEdge(0, 4);
    }

    //generate a 2D4N network with agentNum number of agents
    private void generate2D4N(int agentsNum){
        //TODO
    }



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

    public static void main(String[] args) {
        Network n1 = new Network(5);
        n1.generateN2();
        n1.printDFS(1);




    }
}
