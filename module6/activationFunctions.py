from theano import *
import theano.tensor as T
import numpy as np
import theano.tensor.nnet as Tann

def step(param):
    return T.switch(param < 0, 0, 1)

def sigmoid(param):
    return Tann.sigmoid(param)

def hard_sigmoid(param):
    return Tann.hard_sigmoid(param)

def linear(param):
    return param

def tanh(param):
    return T.tanh(param)

activationFunctions = {
    "sigmoid": sigmoid,
    "hard_sigmoid": hard_sigmoid, 
    "step": step,
    "linear" : linear,
    "tanh" : tanh
    }
