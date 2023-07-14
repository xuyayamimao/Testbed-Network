import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NetworkQ {
    /** agentList stores an ArrayList of Agent object*/
    public ArrayList<AgentQ> agentsList;
    public final int agentCount;//number of total agents in the network, alive or eliminated
    public int aliveAgentCount;//number of alive agents in the network
    public int cooperatorCount;//number of alive cooperators in the network
    //we need to calculate eliminatedPercent and defectorPercent after each trial using the above instance variables

    public int activatedCount;//number of activated agents in the network

    /**
     * Network constructor that initialize a 2D4N network
     * with a central agent as RL agent, all else as cooperator, not RL agents
     * @param numAgents number of agents
     */
    public NetworkQ(int numAgents){
        agentsList = new ArrayList<>();
        agentCount = numAgents;
        aliveAgentCount = numAgents;

        for (int i = 0; i < numAgents; i++) {
            agentsList.add(new AgentQ(i));
        }

        agentsList.get(agentCount/2).activate();

        for (int i = 0; i < agentCount;i++){
            agentsList.get(i).setCooperate(true);
        }
        cooperatorCount = numAgents - 1;
        activatedCount = 1;
        generate2D4NSquare();
    }


    /**
     * 1. Add an undirected edge between agent with index a1 and a2 by updating their respective adjList
     * 2. If a1 == a2, or the edge already exist, do nothing
     * @param a1 index of one agent
     * @param a2 index of another agent
     */
    public void addEdge(int a1, int a2) {
        AgentQ agentQ1 = agentsList.get(a1);
        AgentQ agentQ2 = agentsList.get(a2);
        if (agentQ1.getAdjLists().contains(a2) || a1 == a2){
            return;
        }
        agentQ1.getAdjLists().add(a2);//add a2 to agent1's adjList
        agentQ2.getAdjLists().add(a1);//add a1 to agent2's adjList
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
        AgentQ a = agentsList.get(a1);
        for (Integer i :  a.getAdjLists()) {
            if (i == a2){
                return true;
            }
        }
        return  false;
    }

    public void generate2D4NSquare(){
        for (int i = 0; i <agentCount; i++){
            addEdge(i, (i + 1) % 100 + (i/100)*100);
            addEdge(i, (i - 1 + 100) % 100 + (i/100)*100);
            addEdge(i, (i + 100) % agentCount);
            addEdge(i, (i - 100 + agentCount) % agentCount);
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
            AgentQ a = agentsList.get(i);
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
            AgentQ a = agentsList.get(i);
            if (!a.getEliminated()){
                for (int j = 0; j < a.neighborNum(); j++){
                    int printValue2 = a.getAdjLists().get(j) + 1;
                    trialOutput.write(printValue1 + " " + printValue2 + "\n");
                }
            }
        }//write all edges in the network to file

        //System.out.println("Successful");//print "Successful" if writing is successful
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
            AgentQ a = agentsList.get(i);
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

    public void printAllData(){

        for (int i = 0; i < agentCount; i++){
            if (agentsList.get(i).getCooperate()){
                System.out.print("C    ");
            }else{
                System.out.print("D    ");
            }
        }
        System.out.println();

        for (int i = 0; i < agentCount; i++){
            System.out.print(agentsList.get(i).getActualPayoffs() +"  ");
        }
        System.out.println();

        for (int i = 0; i < agentCount; i++){
            if (agentsList.get(i).getEliminated()){
                System.out.print("T    ");
            }else{
                System.out.print("F    ");
            }

        }
        System.out.println();
    }


    public static void main(String[] args) throws IOException {

    }


}


