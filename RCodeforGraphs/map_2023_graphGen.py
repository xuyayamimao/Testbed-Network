import matplotlib.pyplot as plt
import matplotlib.ticker as mtick
import numpy as np

import os


def genPayoffPlot(actualTotalPayoffFilePath, ifAllCoopTotalPayoffFilePath, trialsFilePath,
                  learning, alpha, b, hybrideOrNot):
    #macbook way of opening file ***possibily also works on windows
    actualPayOffFile = open(os.path.expanduser(actualTotalPayoffFilePath), 'r')
    allCoopPayoffFile = open(os.path.expanduser(ifAllCoopTotalPayoffFilePath), 'r')
    trialsFile = open(os.path.expanduser(trialsFilePath), 'r')


    actualPayoffs = []
    allCoopPayoffs = []
    trials = []

    for row in actualPayOffFile:
        row = row.split('\n')
        if row[0] != '':
            actualPayoffs.append(float(row[0]))
            print(actualPayoffs)
        else:
            break

    for row in allCoopPayoffFile:
        row = row.split('\n')
        if row[0] != '':
            allCoopPayoffs.append(float(row[0]))
            print(allCoopPayoffs)
        else:
            break

    for row in trialsFile:
        row = row.split('\n')
        if row[0] != '':
            trials.append(int(row[0]))
            print(trials)
        else:
            break


    plt.title(learning + "Agent " + "Alpha = " + alpha + " b = " + b + " " + hybrideOrNot + " totalPayoffs")
    plt.plot(trials, actualPayoffs, 'g')
    plt.plot(trials, allCoopPayoffs, 'r')
    plt.xlabel('Trials')
    plt.ylabel('Payoffs')
    #Convert values on y-axis into percentages, comment out this line
    #if we want to plot values of total payoffs into line plot
    #plt.gca().yaxis.set_major_formatter(mtick.PercentFormatter(1.0))
    plt.legend(['Total payoffs', 'Total payoffs if all agents are cooperators'])
    plt.show()

    #add grid or not?

    actualPayOffFile.close()
    trialsFile.close()
    allCoopPayoffFile.close()

#If not in the combo mode, then set allAgBeRL = 0
def CCCDDDPlot (CCFilePath, CDFilePath, DDFilePath, allAgBeRL, trialsFilePath,
                  learning, alpha, b, hybrideOrNot):
    # macbook way of opening file ***possibily also works on windows
    CCFile = open(os.path.expanduser(CCFilePath), 'r')
    CDFile = open(os.path.expanduser(CDFilePath), 'r')
    DDFile = open(os.path.expanduser(DDFilePath), 'r')
    trialsFile = open(os.path.expanduser(trialsFilePath), 'r')

    CCPairs = []
    CDPairs = []
    DDPairs = []
    trials = []

    for row in CCFile:
        row = row.split('\n')
        if row[0] != '':
            CCPairs.append(float(row[0]))
            print(CCPairs)
        else:
            break

    for row in CDFile:
        row = row.split('\n')
        if row[0] != '':
            CDPairs.append(float(row[0]))
            print(CDPairs)
        else:
            break

    for row in DDFile:
        row = row.split('\n')
        if row[0] != '':
            DDPairs.append(float(row[0]))
            print(DDPairs)
        else:
            break

    for row in trialsFile:
        row = row.split('\n')
        if row[0] != '':
            trials.append(int(row[0]))
            print(trials)
        else:
            break

    plt.title(learning + "Agent " + "Alpha = " + alpha + " b = " + b + " " + hybrideOrNot + " C|C, C|D, D|D Pair Percentage")
    plt.plot(trials, CCPairs, 'g')
    plt.plot(trials, CDPairs, 'r')
    plt.plot(trials, DDPairs, 'b')

    #if the mode is in the combo mode
    if allAgBeRL != 0:
        plt.axvline(allAgBeRL, 'b', 'Trial number when all agents become RL agents')

    plt.xlabel('Trials')
    plt.ylabel('Percentage')
    # Convert values on y-axis into percentages, comment out this line
    # if we want to plot values of total payoffs into line plot
    plt.gca().yaxis.set_major_formatter(mtick.PercentFormatter(1.0))
    plt.legend(['C|C percentage', 'C|D percentage', 'D|D percentage'])
    plt.show()


# def aliveRLDumbPlot (aliveAgentPercentFilePath, RLAgentPercentFilePath, dumbAgentPercentFile, trialsFilePath,
#                   learning, alpha, b, hybrideOrNot):


















