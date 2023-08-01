import matplotlib.pyplot as plt
import matplotlib.ticker as mtick
import numpy as np
import math
import os
from pathlib import Path


def genPayoffPlot(phaseValue, actualTotalPayoffFile, ifAllCoopTotalPayoffFile, trialsFileO,
                  learning, alpha, b, hybrideOrNot):
    plt.clf()
    #macbook way of opening file ***possibily also works on windows
    actualPayOffFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + actualTotalPayoffFile + ".txt", 'r')
    allCoopPayoffFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + ifAllCoopTotalPayoffFile + ".txt", 'r')
    trialsFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + trialsFileO + ".txt", 'r')


    actualPayoffs = []
    allCoopPayoffs = []
    trials = []

    for row in actualPayOffFile:
        row = row.split('\n')
        if row[0] != '':
            actualPayoffs.append(float(row[0]))
            #print(actualPayoffs)
        else:
            break

    for row in allCoopPayoffFile:
        row = row.split('\n')
        if row[0] != '':
            allCoopPayoffs.append(float(row[0]))
            #print(allCoopPayoffs)
        else:
            break

    for row in trialsFile:
        row = row.split('\n')
        if row[0] != '':
            trials.append(int(row[0]))
            #print(trials)
        else:
            break


    plt.title(learning + " Agent: " + "α = " + str(alpha) + " b = " + str(b) + " " + hybrideOrNot + " Total Payoffs")
    plt.plot(trials, actualPayoffs, 'g')
    plt.plot(trials, allCoopPayoffs, 'r')
    plt.ylim([0, 42000])
    plt.xlim([0, 2000])
    plt.xlabel('Trials')
    plt.ylabel('Payoffs')
    #Convert values on y-axis into percentages, comment out this line
    #if we want to plot values of total payoffs into line plot
    #plt.gca().yaxis.set_major_formatter(mtick.PercentFormatter(1.0))
    plt.legend(['Total payoffs', 'Total payoffs if all agents are cooperators'])
    #plt.show()

    #add grid or not?

    actualPayOffFile.close()
    trialsFile.close()
    allCoopPayoffFile.close()
    return plt; 

#If not in the combo mode, then set allAgBeRL = 0
def CCCDDDPlot (phaseValue, CCFilePath, CDFilePath, DDFilePath, RLAgentFilePath, trialsFilePath,
                  learning, alpha, b, hybrideOrNot):
    plt.clf()
    # macbook way of opening file ***possibily also works on windows
    CCFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + CCFilePath + ".txt", 'r')
    CDFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + CDFilePath + ".txt", 'r')
    DDFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + DDFilePath + ".txt", 'r')
    RLAgentFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + RLAgentFilePath + ".txt", 'r')
    trialsFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + trialsFilePath + ".txt", 'r')


    CCPairs = []
    CDPairs = []
    DDPairs = []
    RLAgents = []
    trials = []

    for row in CCFile:
        row = row.split('\n')
        if row[0] != '':
            CCPairs.append(float(row[0]))
            #print(CCPairs)
        else:
            break

    for row in CDFile:
        row = row.split('\n')
        if row[0] != '':
            CDPairs.append(float(row[0]))
            #print(CDPairs)
        else:
            break

    for row in DDFile:
        row = row.split('\n')
        if row[0] != '':
            DDPairs.append(float(row[0]))
            #print(DDPairs)
        else:
            break

    for row in RLAgentFile:
        row = row.split('\n')
        if row[0] != '':
            RLAgents.append(float(row[0]))
        else:
            break
    
    j = 0
    for i in range(0, len(RLAgents), 1):
        if math.isclose(RLAgents[i], 1.0):
            j = i + 1
            break



    for row in trialsFile:
        row = row.split('\n')
        if row[0] != '':
            trials.append(int(row[0]))
            #print(trials)
        else:
            break

    plt.title(hybrideOrNot + " " + learning + " Agent: " + "α = " + str(alpha) + " b = " + str(b) + " " +  " C|C, C|D, D|D Pair Percentage")
    plt.plot(trials, CCPairs, 'g')
    plt.plot(trials, CDPairs, 'r')
    plt.plot(trials, DDPairs, 'b')
    plt.ylim([-0.5, 1.5])
    plt.xlim([0, 2000])
    #if the mode is in the combo mode
    if j != 1:
        plt.axvline(j)

    plt.xlabel('Trials')
    plt.ylabel('Percentage')
    # Convert values on y-axis into percentages, comment out this line
    # if we want to plot values of total payoffs into line plot
    plt.gca().yaxis.set_major_formatter(mtick.PercentFormatter(1.0))
    plt.legend(['C|C percentage', 'C|D percentage', 'D|D percentage', 'Trial number when all agents become RL agents ' + "(" + str(j) + ")"])
    #plt.savefig("Hybrid_double_Q-Learning\\Alpha_0.0_b_0.0\\" + hybrideOrNot + learning + "CCCDDDPlot" + "α=" + str(alpha) + "b=" + str(b) + ".png")
    #plt.show()
    #plt.savefig( hybrideOrNot + learning + "CCCDDDPlot" + "α=" + str(alpha) + "b=" + str(b) + ".png")

    CCFile.close()
    CDFile.close()
    DDFile.close()
    RLAgentFile.close()
    trialsFile.close()
    return plt


def RLDumbPlot (phaseValue, aliveAgentPath, RLAgentPath, trialsFilePath,
                  learning, alpha, b, hybrideOrNot):
    plt.clf()
    # macbook way of opening file ***possibily also works on windows
    aliveAgent = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + aliveAgentPath + ".txt", 'r')
    RLAgent = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + RLAgentPath + ".txt", 'r')
    trialsFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + trialsFilePath + ".txt", 'r')

    aliveAgents = []
    RLAgents = []
    trials = []

    for row in  aliveAgent:
        row = row.split('\n')
        if row[0] != '':
            aliveAgents.append(float(row[0]))
            #print(CCPairs)
        else:
            break

    for row in RLAgent:
        row = row.split('\n')
        if row[0] != '':
            RLAgents.append(float(row[0]))
            #print(CDPairs)
        else:
            break

    for row in trialsFile:
        row = row.split('\n')
        if row[0] != '':
            trials.append(float(row[0]))
            #print(DDPairs)
        else:
            break

    arrayAlive = np.array(aliveAgents)
    arrayRL = np.array(RLAgents)

    result = np.subtract(arrayAlive,arrayRL)
    dumbAgents = result.tolist()

    plt.title(hybrideOrNot + " " + learning + " Agent: " + "α = " + str(alpha) + " b = " + str(b) + " " +  "RL, dumb agents percent")
    plt.plot(trials, RLAgents, 'r')
    plt.plot(trials, dumbAgents, 'b')
    plt.ylim([-0.5, 1.5])
    plt.xlim([0, 2000])
    plt.xlabel('Trials')
    plt.ylabel('Percentage')
    # Convert values on y-axis into percentages, comment out this line
    # if we want to plot values of total payoffs into line plot
    plt.gca().yaxis.set_major_formatter(mtick.PercentFormatter(1.0))
    plt.legend(['RL agents percentage', 'Dormant agents percentage'])
    #plt.show()
    aliveAgent.close()
    RLAgent.close()
    trialsFile.close()
    return plt



def alivePlot (phaseValue, aliveAgentPath, trialsFilePath,
                  learning, alpha, b, hybrideOrNot):
    plt.clf()
    aliveAgent = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + aliveAgentPath + ".txt", 'r')
    trialsFile = open("C:\\Users\\xuziya\\Testbed-Network\\Phase" + str(phaseValue) + "\\experimentAlpha" + str(alpha) + "b" + str(b) + "\\" + trialsFilePath + ".txt", 'r')
    aliveAgents = []
    trials = []
    
    for row in  aliveAgent:
        row = row.split('\n')
        if row[0] != '':
            aliveAgents.append(float(row[0]))
            #print(CCPairs)
        else:
            break
    
    for row in trialsFile:
        row = row.split('\n')
        if row[0] != '':
            trials.append(float(row[0]))
            #print(DDPairs)
        else:
            break
    
    plt.title(hybrideOrNot + " " + learning + " Agent: " + "α = " + str(alpha) + " b = " + str(b) + " " +  "Alive agents percent")
    plt.plot(trials, aliveAgents, 'g')
    plt.xlabel('Trials')
    plt.ylabel('Percentage')
    plt.ylim([-0.5, 1.5])
    plt.xlim([0, 2000])
    plt.gca().yaxis.set_major_formatter(mtick.PercentFormatter(1.0))
    plt.legend(['Alive agents percentage'])
    #plt.show()
    aliveAgent.close()
    trialsFile.close()

    return plt


#plot:CCCDDDplot, etc
def photoSave(phase, alpha, b, plot, plt):
    if phase == 2:
        plt.savefig("Hybrid_Q-Learning\\Alpha_" + str(alpha) + "_b_" + str(b) + "\\" + "Hybrid_Q-learning_" + plot + "_" + "α=" + str(alpha) + "b=" + str(b) + ".png")
    elif phase == 3:
        plt.savefig("Hybrid_double_Q-Learning\\Alpha_" + str(alpha) + "_b_" + str(b) + "\\" + "Hybrid_double_Q-learning_" + plot + "_" + "α=" + str(alpha) + "b=" + str(b) + ".png")
    elif phase == 4:
        plt.savefig("Q-Learning\\Alpha_" + str(alpha) + "_b_" + str(b) + "\\" + "Single_Q-learning_" + plot + "_" + "α=" + str(alpha) + "b=" + str(b) + ".png")
    elif phase == 5:
        plt.savefig("Double_Q-Learning\\Alpha_" + str(alpha) + "_b_" + str(b) + "\\" + "Single_double_Q-learning_" + plot + "_" + "α=" + str(alpha) + "b=" + str(b) + ".png")



# read all the file from a folder(experimentAlphaXby), with value of phase, alpha, and b to locate the folder
def readAllGenAll(phase, alpha, b):
    if phase == 2:
        photo1 = genPayoffPlot(phase, "payoffSum", "payoffSumIfAllCoop", "trialNum", "Q-Learning", alpha, b, "Hybrid")
        photoSave(phase, alpha, b, "genPayoffPlot", photo1)
        photo2 = CCCDDDPlot(phase, "CCRecord", "CDRecord", "DDRecord", "RLAgent", "trialNum", "Q-Learning", alpha, b, "Hybrid")
        photoSave(phase, alpha, b, "CCCDDDPlot", photo2)
        photo3 = RLDumbPlot(phase, "aliveAgent", "RLAgent", "trialNum", "Q-Learning", alpha, b, "Hybrid")
        photoSave(phase, alpha, b, "RLDumbPlot", photo3)
        photo4 = alivePlot(phase, "aliveAgent", "trialNum", "Q-Learning", alpha, b, "Hybrid")
        photoSave(phase, alpha, b, "alivePlot", photo4)
    elif phase == 3:
        photo1 = genPayoffPlot(phase, "payoffSum", "payoffSumIfAllCoop", "trialNum", "Double_Q-Learning", alpha, b, "Hybrid")
        photoSave(phase, alpha, b, "genPayoffPlot", photo1)
        photo2 = CCCDDDPlot(phase, "CCRecord", "CDRecord", "DDRecord", "RLAgent", "trialNum", "Double_Q-Learning", alpha, b, "Hybrid")
        photoSave(phase, alpha, b, "CCCDDDPlot", photo2)
        photo3 = RLDumbPlot(phase, "aliveAgent", "RLAgent", "trialNum", "Double_Q-Learning", alpha, b, "Hybrid")
        photoSave(phase, alpha, b, "RLDumbPlot", photo3)
        photo4 = alivePlot(phase, "aliveAgent", "trialNum", "Double_Q-Learning", alpha, b, "Hybrid")
        photoSave(phase, alpha, b, "alivePlot", photo4)
    elif phase == 4:
        photo1 = genPayoffPlot(phase, "payoffSum", "payoffSumIfAllCoop", "trialNum", "Q-Learning", alpha, b, "Single")
        photoSave(phase, alpha, b, "genPayoffPlot", photo1)
        photo2 = CCCDDDPlot(phase, "CCRecord", "CDRecord", "DDRecord", "RLAgent", "trialNum", "Q-Learning", alpha, b, "Single")
        photoSave(phase, alpha, b, "CCCDDDPlot", photo2)
        photo3 = RLDumbPlot (phase, "aliveAgent", "RLAgent", "trialNum", "Q-Learning", alpha, b, "Single")
        photoSave(phase, alpha, b, "RLDumbPlot", photo3)
        photo4 = alivePlot (phase, "aliveAgent", "trialNum", "Q-Learning", alpha, b, "Single")
        photoSave(phase, alpha, b, "alivePlot", photo4)
        
    elif phase == 5:
        photo1 = genPayoffPlot(phase, "payoffSum", "payoffSumIfAllCoop", "trialNum", "Double_Q-Learning", alpha, b, "Single")
        photoSave(phase, alpha, b, "genPayoffPlot", photo1)
        photo2 = CCCDDDPlot(phase, "CCRecord", "CDRecord", "DDRecord", "RLAgent", "trialNum", "Double_Q-Learning", alpha, b, "Single")
        photoSave(phase, alpha, b, "CCCDDDPlot", photo2)
        photo3 = RLDumbPlot (phase, "aliveAgent", "RLAgent", "trialNum", "Double_Q-Learning", alpha, b, "Single")
        photoSave(phase, alpha, b, "RLDumbPlot", photo3)
        photo4 = alivePlot (phase, "aliveAgent", "trialNum", "Double_Q-Learning", alpha, b, "Single")
        photoSave(phase, alpha, b, "alivePlot", photo4)












Path("Hybrid_Q-Learning").mkdir(parents = True, exist_ok = True)
Path("Hybrid_double_Q-Learning").mkdir(parents = True, exist_ok = True)
Path("Q-Learning").mkdir(parents = True, exist_ok = True)
Path("Double_Q-Learning").mkdir(parents = True, exist_ok = True)

Path("Hybrid_Q-Learning\\Alpha_0.0_b_0.0").mkdir(parents = True, exist_ok = True)
Path("Hybrid_Q-Learning\\Alpha_0.2_b_0.9").mkdir(parents = True, exist_ok = True)
Path("Hybrid_Q-Learning\\Alpha_0.2_b_1.0").mkdir(parents = True, exist_ok = True)
Path("Hybrid_Q-Learning\\Alpha_0.2_b_1.1").mkdir(parents = True, exist_ok = True)
Path("Hybrid_double_Q-Learning\\Alpha_0.0_b_0.0").mkdir(parents = True, exist_ok = True)
Path("Hybrid_double_Q-Learning\\Alpha_0.2_b_0.9").mkdir(parents = True, exist_ok = True)
Path("Hybrid_double_Q-Learning\\Alpha_0.2_b_1.0").mkdir(parents = True, exist_ok = True)
Path("Hybrid_double_Q-Learning\\Alpha_0.2_b_1.1").mkdir(parents = True, exist_ok = True)
Path("Q-Learning\\Alpha_0.0_b_0.0").mkdir(parents = True, exist_ok = True)
Path("Q-Learning\\Alpha_0.2_b_0.9").mkdir(parents = True, exist_ok = True)
Path("Q-Learning\\Alpha_0.2_b_1.0").mkdir(parents = True, exist_ok = True)
Path("Q-Learning\\Alpha_0.2_b_1.1").mkdir(parents = True, exist_ok = True)
Path("Double_Q-Learning\\Alpha_0.0_b_0.0").mkdir(parents = True, exist_ok = True)
Path("Double_Q-Learning\\Alpha_0.2_b_0.9").mkdir(parents = True, exist_ok = True)
Path("Double_Q-Learning\\Alpha_0.2_b_1.0").mkdir(parents = True, exist_ok = True)
Path("Double_Q-Learning\\Alpha_0.2_b_1.1").mkdir(parents = True, exist_ok = True)

#for i in range(1, 5, 1): #phase 
for j in range(0, 4, 1):
    if j == 0:
        readAllGenAll(5, 0.0, 0.0)
    elif j == 1:
        readAllGenAll(5, 0.2, 0.9)
    elif j == 2:
        readAllGenAll(5, 0.2, 1.0)
    elif j == 3:
        readAllGenAll(5, 0.2, 1.1)




    


# genPayoffPlot("C:\\Users\\xuziya\\Testbed-Network\\Phase2\\experimentAlpha0.0b0.0\\payoffSum.txt", "C:\\Users\\xuziya\\Testbed-Network\\Phase2\\experimentAlpha0.0b0.0\\payoffSumIfAllCoop.txt",
#                     "C:\\Users\\xuziya\\Testbed-Network\\Phase2\\experimentAlpha0.0b0.0\\trialNum.txt",
#                   "Q-Learning", 0.2, 0.9, "Combo")

# CCCDDDPlot ("C:\\Users\\xuziya\\Testbed-Network\\Phase2\\experimentAlpha0.0b0.0\\CCRecord.txt", "C:\\Users\\xuziya\\Testbed-Network\\Phase2\\experimentAlpha0.0b0.0\\CDRecord.txt", 
#             "C:\\Users\\xuziya\\Testbed-Network\\Phase2\\experimentAlpha0.0b0.0\\DDRecord.txt", 500, "C:\\Users\\xuziya\\Testbed-Network\\Phase2\\experimentAlpha0.0b0.0\\trialNum.txt",
#                  "Q-Learning", 0.0, 0.0, "Combo")

# genPayoffPlot(2, "payoffSum", "payoffSumIfAllCoop",
#                     "trialNum",
#                   "Q-Learning", 0.0, 0.0, "Combo")

#CCCDDDPlot(2, "CCRecord", "CDRecord", "DDRecord", "RLAgent", "trialNum", "Q-Learning", 0.0, 0.0, "Hybrid")



# RLDumbPlot (2, "aliveAgent", "RLAgent", "trialNum",
#                   "Q-Learning", 0.0, 0.0, "Hybrid")

# alivePlot (2, "aliveAgent", "trialNum",
#                   "Q-Learning", 0.0, 0.0, "Hybrid")






















