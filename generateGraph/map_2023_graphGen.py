import matplotlib.pyplot as plt
import matplotlib.ticker as mtick
import numpy as np

import os



#open txt file, need to change file path if used on window
scoreFile = open(os.path.expanduser("~/Desktop/SummerMAP/test_pycharm_open.txt"), 'r')
trialsFile = open(os.path.expanduser("~/Desktop/SummerMAP/test_pycharm_trials.txt"), 'r')
scores = []
trials = []
for row in scoreFile:
    row = row.split('\n')
    if row[0] != '':
        scores.append(float(row[0]))
        print(scores)
    else:
        break

for row in trialsFile:
    row = row.split('\n')
    if row[0] != '':
        trials.append(int(row[0]))
        print(trials)
    else:
        break


plt.title("Scores and trials")
plt.plot(trials, scores, 'g')
#Convert values on y-axis into percentages, comment out this line
#if we want to plot values of total payoffs into line plot
#plt.gca().yaxis.set_major_formatter(mtick.PercentFormatter(1.0))
plt.xlabel('Trials')
plt.ylabel('Scores')
plt.legend(['What'])




plt.show()

#add grid or not?

scoreFile.close()
trialsFile.close()









