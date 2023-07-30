package Phase5;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AllNetworkDQ {
    /**
     * agentList stores an ArrayList of Agent object
     */
    public ArrayList<AllAgentDQ> agentsList;
    public ArrayList<Integer> RLAgentList;//a list of indexes of RL agents
    public final int agentCount;//number of total agents in the network, alive or eliminated
    public int aliveAgentCount;//number of alive agents in the network
    public int cooperatorCount;//number of alive cooperators in the network
    //we need to calculate eliminatedPercent and defectorPercent after each trial using the above instance variables


    /**
     * Network constructor that initialize a 2D4N network
     * with a central agent as RL agent, all else as cooperator, not RL agents
     *
     * @param numAgents number of agents
     */
    public AllNetworkDQ(int numAgents) {
        agentsList = new ArrayList<>();
        RLAgentList = new ArrayList<>();
        agentCount = numAgents;
        aliveAgentCount = numAgents;
        //add numAgents number of agents to the network, and activate all as Double-Q-Learning agents
        for (int i = 0; i < numAgents; i++) {
            AllAgentDQ allAgentDQ = new AllAgentDQ(i);
            agentsList.add(allAgentDQ);
            allAgentDQ.activate();
            RLAgentList.add(i);//update RLAgentList
            //if the new agent is cooperator, update cooperatorCount
            if(allAgentDQ.getCooperate()){
                cooperatorCount++;
            }
        }
        generate2D4NSquare();
        initializeQNeighborList();//initialize every AgentQ's QNeighborList when generating a new NetworkQ
    }


    /**
     * 1. Add an undirected edge between agent with index a1 and a2 by updating their respective adjList
     * 2. If a1 == a2, or the edge already exist, do nothing
     *
     * @param a1 index of one agent
     * @param a2 index of another agent
     */
    public void addEdge(int a1, int a2) {
        AllAgentDQ agentQ1 = agentsList.get(a1);
        AllAgentDQ agentQ2 = agentsList.get(a2);
        if (agentQ1.getAdjLists().contains(a2) || a1 == a2) {
            return;
        }
        agentQ1.getAdjLists().add(a2);//add a2 to agent1's adjList
        agentQ2.getAdjLists().add(a1);//add a1 to agent2's adjList
    }


    /**
     * 1. Check if two agents are adjacent
     * 2. If a2 = a2, or one of the agent is already eliminated, return false
     *
     * @param a1 index of one agent
     * @param a2 index of another agent
     * @return boolean of whether a1 and a2 are adjacent
     */
    public boolean isAdjacent(int a1, int a2) {
        if (agentsList.get(a1).getEliminated() || agentsList.get(a2).getEliminated()) return false;
        if (a1 == a2) {
            return false;
        }
        AllAgentDQ a = agentsList.get(a1);
        for (Integer i : a.getAdjLists()) {
            if (i == a2) {
                return true;
            }
        }
        return false;
    }

    /**
     * initialize the NetworkQ as a square 2D4N network with 10000 agents
     * same as the cascading failure paper
     */
    public void generate2D4NSquare() {
        for (int i = 0; i < agentCount; i++) {
            addEdge(i, (i + 1) % 100 + (i / 100) * 100);
            addEdge(i, (i - 1 + 100) % 100 + (i / 100) * 100);
            addEdge(i, (i + 100) % agentCount);
            addEdge(i, (i - 100 + agentCount) % agentCount);
        }
    }

    /**
     * initialize the AllNetworkQ as a linear 2D4N network for testing
     */
    public void generateTest2D4N() {
        for (int i = 0; i < agentCount; i++) {
            addEdge(i, (i + 1) % agentCount);
            addEdge(i, (i + 2) % agentCount);
            addEdge(i, (i - 1 + agentCount) % agentCount);
            addEdge(i, (i - 2 + agentCount) % agentCount);
            //System.out.println((i - 2 + agentCount) % agentCount);
        }
    }

    /**
     * Initialize every AgentQ's QNeighborList as their initial neighbor list with four neighbors
     */
    public void initializeQNeighborList() {
        for (int i = 0; i < agentCount; i++) {
            AllAgentDQ agentQ = agentsList.get(i);
            for (int j = 0; j < 4; j++) {
                int neighborIndex = agentQ.getAdjLists().get(j);
                AllAgentDQ neighbor = agentsList.get(neighborIndex);
                agentQ.addToQNeighborList(neighbor);
            }
        }
    }

    /**
     * Print the given network to a file with filename in the format of Pajek net file
     *
     * @param filename String of file name
     * @throws IOException if the file cannot be opened or closed
     */
    public void printNetworkToFile(String filename) throws IOException {
        FileWriter trialOutput = new FileWriter(filename);//open file for writing
        trialOutput.write("*Vertices " + agentCount + "\n");
        for (int i = 0; i < agentCount; i++) {
            int printValue = i + 1;
            trialOutput.write(printValue + " " + "\"" + printValue + "\" ic ");
            AllAgentDQ a = agentsList.get(i);
            if (a.getEliminated()) {
                trialOutput.write("Gray\n");
            } else {
                if (a.getCooperate()) {
                    trialOutput.write("Blue\n");
                } else {
                    trialOutput.write("Black\n");
                }
            }
        }//write all vertices in the network to file

        trialOutput.write("*Edges\n");
        for (int i = 0; i < agentCount; i++) {
            int printValue1 = i + 1;
            AllAgentDQ a = agentsList.get(i);
            if (!a.getEliminated()) {
                for (int j = 0; j < a.neighborNum(); j++) {
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
    public void printNetwork() {
        for (int i = 0; i < agentCount; i++) {
            System.out.print(agentsList.get(i).getCooperate() + " ");
        }
        System.out.print("\n");

        for (int i = 0; i < agentCount; i++) {
            System.out.print(agentsList.get(i).getEliminated() + " ");
        }
        System.out.print("\n");

        for (int i = 0; i < agentCount; i++) {
            for (int j = 0; j < agentCount; j++) {
                if (isAdjacent(i, j)) {
                    System.out.print("1 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.print("\n");
        }
    }

    public void printAllData() {

        for (int i = 0; i < agentCount; i++) {
            if (agentsList.get(i).getCooperate()) {
                System.out.print("C    ");
            } else {
                System.out.print("D    ");
            }
        }
        System.out.println();

        for (int i = 0; i < agentCount; i++) {
            System.out.print(agentsList.get(i).getActualPayoffs() + "  ");
        }
        System.out.println();

        for (int i = 0; i < agentCount; i++) {
            if (agentsList.get(i).getEliminated()) {
                System.out.print("T    ");
            } else {
                System.out.print("F    ");
            }

        }
        System.out.println();
        for (int i = 0; i < RLAgentList.size(); i++) {
            System.out.print(RLAgentList.get(i) + " ");
        }

    }


    public static void main(String[] args) throws IOException {

    }


}


