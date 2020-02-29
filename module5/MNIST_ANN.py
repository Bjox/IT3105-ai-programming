from theano import *
import theano.tensor as T
import numpy as np
import theano.tensor.nnet as Tann
from activationFunctions import *

try :
	import cprogressbar as P
except ImportError:
	import progressbar as P

DEBUG = False

# make sure arrays are CUDA compatible
def floatX(X):
	return np.asarray(X, dtype=theano.config.floatX)


class MNIST_ANN:
	def __init__(self, inputNodes=784, outputNodes=10, hiddenLayers=[20], learningRate=0.2, hiddenFunc="sigmoid", outputFunc="sigmoid"):
		self.inputNodes = inputNodes
		self.outputNodes = outputNodes
		self.learningRate = learningRate
		self.hiddenLayers = hiddenLayers
		self.weights = []
		self.biases = []
		self.X = [] # TODO: better name?
		self.hiddenFunc = hiddenFunc
		self.outputFunc = outputFunc
		# make the brainz
		self.buildAnn()

	def hiddenFunction(self, param):
		return activationFunctions[self.hiddenFunc](param)

	def outputFunction(self, param):
		return activationFunctions[self.outputFunc](param)

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
			self.X = [self.outputFunction(T.dot(input, self.weights[0]) + self.biases[0])]
		else:
			self.X.append(self.hiddenFunction(T.dot(input, self.weights[0]) + self.biases[0]))
			lim = len(self.hiddenLayers)-1
			for i in range(lim):
				self.X.append(self.hiddenFunction(T.dot(self.X[i], self.weights[i+1]) + self.biases[i+1]))
			self.X.append(self.outputFunction(T.dot(self.X[lim], self.weights[lim+1]) + self.biases[lim+1]))

		error = T.sum((answer - self.X[-1])**2) # this -1 index, wtf... python is not a real programming language, end of story
		params = []
		for i in range(len(self.weights)):
			params.append(self.weights[i])
			params.append(self.biases[i])
		gradients = T.grad(error, params)
		backprop_acts = [(p, p - self.learningRate*g) for p,g in zip(params, gradients)]

		self.predictor = theano.function([input], self.X[::-1]) # <-- again... what the hell? Unreadable crap
		self.trainer = theano.function([input, answer], error, updates=backprop_acts)

		if DEBUG:
			pass
		return

	def do_training(self, cases, labels, epochs=100):
		errors = []
		maxx = epochs * len(cases)
		dist = 0
		P.progressbar(50, dist, maxx)
		for i in range(epochs):
			error = 0
			for img, lab in zip(cases, labels):
				dist += 1
				P.progressbar(50, dist, maxx)
				ans = np.zeros(10, dtype=tensor.config.floatX)
				ans[lab] = 1.0
				error += self.trainer(floatX(img), ans)
			errors.append(error / len(cases))
		P.progressbar(50, maxx, maxx)
		print()
		return errors

	def predict(self, case):
		res = self.predictor(floatX(case))
		best_ans = 0
		for i in range(len(res[0])):
			if res[0][i] > res[0][best_ans]: best_ans = i
		return best_ans

	def scale(self, cases):
		for i in range(len(cases)):
			new = [x / 255.0 for x in cases[i]]
			cases[i] = new
		return cases

	def blind_test(self, collection):
		# collection is list of sublists
		collection = self.scale(collection)
		# res is a flat list of labels
		res = [self.predict(i) for i in collection]
		return res
