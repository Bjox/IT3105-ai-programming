print("running... ")
print("Theano init ...")

#import mnistdemo

from mnist_basics import *
from MNIST_ANN import *
import time
import matplotlib.pyplot as plt
import configparser
import re
import sys

cfgfile = "runspec.cfg"
if len(sys.argv) >= 2:
    cfgfile = sys.argv[1]
    print("Reading config file:", cfgfile)

try:
    import cprogressbar as P
except ImportError:
    import progressbar as P


def scale(cases):
    for i in range(len(cases)):
        new = [x / 255.0 for x in cases[i]]
        cases[i] = new
    return cases

def get_cases(type="training", N=-1):
    cases, labels = gen_flat_cases(type=type)
    cases = scale(cases)
    return cases, labels

def test(net, testcases, testlabels, N, type="test"):
    numerrs = 0
    numtot = 0
    dist = 0
    P.progressbar(50, dist, N)
    for case, label in zip(testcases, testlabels):
        dist += 1
        numtot += 1
        res = net.predict(case)
        if label != res:
            numerrs += 1
        P.progressbar(50, dist, N)
    P.progressbar(50, N, N)
    print()
    typstr = "[" + type + "]"
    rate = 100 - (numerrs * 100 / numtot)
    print("{:<10} Incorrect results: {} of {}".format(typstr, numerrs, numtot))
    print("{:<10} Success rate:      {:.2f}%".format(typstr, rate))
    return rate

########################################################################################################

cfg = configparser.ConfigParser()
cfg.read(cfgfile)

hiddenLayers = [int(n) for n in re.sub("(\[|\s|\])", "", cfg['NET']['hiddenLayers']).split(",")]
learningRate = float(cfg['NET']['learningRate'])
numTrainCases = int(cfg['NET']['numTrainCases'])
numTestCases = int(cfg['NET']['numTestCases'])
epochs = int(cfg['NET']['epochs'])
hiddenFunction = cfg['NET']['hiddenFunction']
outputFunction = cfg['NET']['outputFunction']
plot = cfg.getboolean('NET','plot')
rowlabel = cfg.get('NET', 'rowlabel') if cfg.has_option('NET', 'rowlabel') else ""
runs = cfg.getint('NET', 'runs') if cfg.has_option('NET', 'runs') else 1
logToDb = cfg.getboolean('NET','logToDB') if cfg.has_option('NET', 'logToDB') else True

########################################################################################################

print("CONFIGURATION: ", cfgfile)
print("\tHidden layer structure:   {}".format(hiddenLayers))
print("\tLearning rate:            {}".format(learningRate))
print("\tNumber of epochs:         {}".format(epochs))
print("\tHidden function:          {}".format(hiddenFunction))
print("\tOutput function:          {}".format(outputFunction))
print("\tPlot error terms:         {}".format(plot))
print("\tTotal number of runs:     {}".format(runs))
print("\tLog results to DB:        {}".format(logToDb))
print()

print("Getting training cases ...")
traincases, trainlabels = get_cases("training", N=numTrainCases)  # Fetch first N number of training cases
print("Getting test cases ...")
testcases, testlabels = get_cases("testing", N=numTestCases)
print()

for i in range(runs):
    print("=== RUN #{}".format(i+1))
    print("Building network ...")
    ann = MNIST_ANN(hiddenLayers=hiddenLayers, learningRate=learningRate)
    print()

    start_t = time.time()

    print("Training ...")
    err = ann.do_training(cases=traincases, labels=trainlabels, epochs=epochs)
    training_t = time.time() - start_t
    print("Elapsed time: {}s\n".format(int(training_t)))

    #print("\nTesting on training set ...")
    #successTraining = test(ann, traincases, trainlabels, numTrainCases, "training")
    #print("Elapsed time: {}s".format(int(time.time() - start_t)))

    #print("\nTesting on test set ...")
    #successTesting = test(ann, testcases, testlabels, numTestCases, "testing")
    #elapsed_t = time.time() - start_t
    #print("Elapsed time: {}s".format(int(elapsed_t)))

    #print("Error: {:.5f}\n\n".format(err[-1]))

	
    #mnistdemo.major_demo(ann, r, "./")
    minor_demo(ann)
	

if plot:
    plt.plot(err)
    plt.show()

print("Done")
