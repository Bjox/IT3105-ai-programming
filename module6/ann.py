from theano import *
import theano.tensor as T
import numpy as np
import theano.tensor.nnet as Tann
from activationFunctions import *
import cprogressbar as P



# make sure arrays are CUDA compatible
def floatX(X):
    return np.asarray(X, dtype=theano.config.floatX)

def snakeLength(state):
    length = 1
    ls = state[0:4]
    ls.extend(list(reversed(state[4:8])))
    cell = ls[0]
    for i in range(len(ls)-1):
        if ls[i+1] == cell-1:
            length += 1
            cell = ls[i+1]
        else: return length
    return length

def mergeable(state):
    ls = state[0:4]
    ls.extend(list(reversed(state[4:8])))
    for i in range(len(ls)-1):
        if ls[i] == ls[i+1]: return 1
    return 0

def sorted(state):
    ls = state[0:4]
    ls.extend(list(reversed(state[4:8])))
    for i in range(len(ls)-1):
        if ls[i] < ls[i+1]: return 0
    return 1

class ANN:
    def __init__(self, actfunctions=[], inputNodes=3, outputNodes=4, hiddenLayers=[20], learningRate=0.2):
        self.inputNodes = inputNodes
        self.outputNodes = outputNodes
        self.learningRate = learningRate
        self.hiddenLayers = hiddenLayers
        self.weights = []
        self.biases = []
        self.X = []
        self.actfunctions = []

        for i in range(len(hiddenLayers)+1):
            self.actfunctions.append(activationFunctions[actfunctions[i] if i < len(actfunctions) else "sigmoid"])

        self.buildAnn()

    #def hiddenFunction(self, param):
    #    return activationFunctions[self.hiddenFunc](param)

    #def outputFunction(self, param):
    #    return activationFunctions[self.outputFunc](param)

    def buildAnn(self):
        # connect all layers
        if len(self.hiddenLayers) == 0:
            self.weights = [theano.shared(floatX(np.random.uniform(-0.1, 0.1, size=(self.inputNodes, self.outputNodes))))]
        else:
            last = self.inputNodes
            for i in range(len(self.hiddenLayers)):
                self.weights.append(theano.shared(floatX(np.random.uniform(-0.1, 0.1, size=(last, self.hiddenLayers[i])))))
                last = self.hiddenLayers[i]
            self.weights.append(theano.shared(floatX(np.random.uniform(-0.1, 0.1, size=(last, self.outputNodes)))))

        input = T.vector('input', dtype=theano.config.floatX)
        answer = T.vector('answer', dtype=theano.config.floatX)

        # create biases
        self.biases = [theano.shared(floatX(np.random.uniform(-0.1, 0.1, size=i))) for i in self.hiddenLayers]
        self.biases.append(theano.shared(floatX(np.random.uniform(-0.1, 0.1, size=self.outputNodes))))

        # activation functions
        if len(self.hiddenLayers) == 0:
            self.X = [self.actfunctions[0](T.dot(input, self.weights[0]) + self.biases[0])]
        else:
            self.X.append(self.actfunctions[0](T.dot(input, self.weights[0]) + self.biases[0]))
            lim = len(self.hiddenLayers)-1
            for i in range(lim):
                self.X.append(self.actfunctions[i+1](T.dot(self.X[i], self.weights[i+1]) + self.biases[i+1]))
            self.X.append(self.actfunctions[-1](T.dot(self.X[lim], self.weights[lim+1]) + self.biases[lim+1]))

        error = T.sum((answer - self.X[-1])**2)
        params = []
        for i in range(len(self.weights)):
            params.append(self.weights[i])
            params.append(self.biases[i])
        gradients = T.grad(error, params)
        backprop_acts = [(p, p - self.learningRate*g) for p,g in zip(params, gradients)]

        self.predictor = theano.function([input], self.X[::-1])
        self.trainer = theano.function([input, answer], error, updates=backprop_acts)
        return


    # 0: UP
    # 1: RIGHT
    # 2: DOWN
    # 3: LEFT
    def do_training(self, states, moves, epochs=100):
        errors = []
        error = 0
        err_samples = 100
        maxx = epochs * len(states)
        count = 0
        dist = 0
        states = floatX(self.scaleall(states))

        P.progressbar(50, dist, maxx)
        for i in range(epochs):
            for state, lab in zip(states, moves):
                dist += 1
                P.progressbar(50, dist, maxx)
               
                ans = np.zeros(4, dtype=tensor.config.floatX)
                ans[lab] = 1.0
               
                error += self.trainer(state, ans)
                if count == maxx // err_samples:
                    errors.append(error / count)
                    error = 0
                    count = 0
                count += 1
        P.progressbar(50, maxx, maxx)
        print()
        return errors

    # returns the tuple (predicted_move, raw_nnet_output)
    def predict(self, state):
        res = self.predictor(floatX(state))
        best_ans = 0
        for i in range(len(res[0])):
            if res[0][i] > res[0][best_ans]: best_ans = i
        return best_ans, res

    # case is a list of integers
    def scale(self, case):
        m = max(case)
        scaled = [i/m for i in case]
        return scaled

    def scaleall(self, states):
        scaled = [self.scale(i) for i in states]
        return scaled