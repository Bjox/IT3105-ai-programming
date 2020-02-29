print("ehlo")

from ann import *
from game import *
from gui import *
from threading import *
import time
import random
import sys
import pymysql
import configparser
import cprogressbar as P
import operator
import matplotlib.pyplot as plt


########################################################################################################
conn = pymysql.connect(
    host = 'yeskis.com',
    user='theano',
    password='bkxHR9tJJqtA9kXH',
    db='module6'
    )

cfgfile = "runspec.cfg"
if len(sys.argv) >= 2:
    cfgfile = sys.argv[1]
    print("Reading config file:", cfgfile)

cfg = configparser.ConfigParser()
cfg.read(cfgfile)

hiddenLayers = [int(n) for n in re.sub("(\[|\s|\])", "", cfg['NET']['hiddenLayers']).split(",")]
actfunctions = [] if cfg['NET']['actfunctions'] == "" else [a for a in re.sub("(\[|\s|\])", "", cfg['NET']['actfunctions']).split(",")]
learningRate = float(cfg['NET']['learningRate'])
numTrainCases = int(cfg['NET']['numTrainCases'])
numTestCases = int(cfg['NET']['numTestCases'])
epochs = int(cfg['NET']['epochs'])
plot = cfg.getboolean('NET','plot')
rowlabel = cfg.get('NET', 'rowlabel') if cfg.has_option('NET', 'rowlabel') else ""
runs = cfg.getint('NET', 'runs') if cfg.has_option('NET', 'runs') else 1
logToDb = cfg.getboolean('NET','logToDB') if cfg.has_option('NET', 'logToDB') else True
graphics = cfg.getboolean('NET','graphics') if cfg.has_option('NET', 'graphics') else True

print("CONFIGURATION: ", cfgfile)
print("\tHidden layer structure:   {}".format(hiddenLayers))
print("\tLearning rate:            {}".format(learningRate))
print("\tActivation functions:     {}".format(actfunctions))
print("\tNumber of training cases: {}".format(numTrainCases))
print("\tNumber of testing cases:  {}".format(numTestCases))
print("\tNumber of epochs:         {}".format(epochs))
print("\tPlot error terms:         {}".format(plot))
print("\tTotal number of runs:     {}".format(runs))
print("\tLog results to DB:        {}".format(logToDb))
print()

########################################################################################################

trainingfilestr = "training.txt"
trainingfile = open(trainingfilestr, 'r')
trainingstates = []
trainingmoves = []
lines = []
for line in trainingfile:
    lines.append(line)
linecount = 0

if numTrainCases < 0: numTrainCases = len(lines)
trainingfile.close()
print("\nNumber of training cases: {}".format(len(lines)))
print("Parsing training case file ...")

for line, c in zip(lines, range(numTrainCases)):
    if len(trainingstates) == 30000: break

    P.progressbar(50, c, numTrainCases)
    split = line.split(":")
    
    state = [int(i) for i in split[0].split(",")]
    L = snakeLength(state)
    M = mergeable(state)
    S = sorted(state)

    trainingstates.append([L, M, S])
    trainingmoves.append(int(split[1]))
P.progressbar(50, 1, 1)
print("\n")

for i in range(runs):
    print("=== RUN #{}".format(i+1))
    print("Building network ...")
    ann = ANN(hiddenLayers=hiddenLayers, learningRate=learningRate, actfunctions=actfunctions)
    print()

    start_t = time.time()

    print("Training ...")
    err = ann.do_training(states=trainingstates, moves=trainingmoves, epochs=epochs)
    training_t = time.time() - start_t
    print("Elapsed time: {}s\n".format(int(training_t)))

    if plot:
        plt.plot(err)
        plt.show()

    #print("\nTesting on training set ...")
    #successTraining = test(ann, trainingstates, trainingmoves, numTrainCases, "training")
    #print("Elapsed time: {}s".format(int(time.time() - start_t)))
    #print("\nTesting on test set ...")
    #successTesting = test(ann, testcases, trainingmoves, numTestCases, "testing")
    #elapsed_t = time.time() - start_t
    #print("Elapsed time: {}s".format(int(elapsed_t)))
    #print("Error: {:.5f}\n\n".format(err[-1]))

    game = Game()
    game.newgame()

    if graphics:
        gw = GameWindow()
        gw.update_view(game.getboard())
        gw.update()

    while not game.isgameover():
        #ann.scale(game.getboard())
        #mov = random.randint(0, 3)
        state = game.getboard()
        L = snakeLength(state)
        M = mergeable(state)
        S = sorted(state)
        _,values = ann.predict([L, M, S])
        vals = [(v,m) for (v,m) in zip(values[0].tolist(), range(len(values[0])))]
        vals.sort()

        for a, b in vals:
            print("| {0:2f}:{1} ".format(a,b), end="")
        print("|")

        mov = vals.pop()[1]
        while not game.move(mov):
            mov = vals.pop()[1]

        game.spawn()
        if graphics:
            gw.update_view(game.getboard())
            gw.update()
            time.sleep(0.01)
    

print("Game over! Highest tile achieved:", game.getMaxTile())
game.printBoard()

if graphics:
    while True:
        try: gw.update()
        except TclError: exit()
