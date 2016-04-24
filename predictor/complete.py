#!usr/bin/python

import numpy as np
from sklearn.naive_bayes import GaussianNB
from sklearn.preprocessing import normalize
import socket
import threading
import StringIO

def worker(socket):
    command = socket.recv(8)
    if (command == 'FE'): # check value from message
        # batch training
        data = socket.recv(1024)
        batch = np.genfromtxt(StringIO(data),usecols=(0,1,2,3,4))
        X_batch = batch[:,0]
        y_batch = batch[:,[1,3,4]]
        
        # translate continuous to discrete output for classifier
        y_batch[y_batch < 15] = 1
        y_batch[np.logical_and(y_batch >= 15, y_batch < 30)] = 2
        y_batch[np.logical_and(y_batch >= 30, y_batch < 60)] = 3
        y_batch[y_batch >= 60] = 4
        
        # trains the model
        clf.partial_fit(X_batch, y_batch)
    elif (command == 'PR'):
        # prediction
        wind = float(socket.recv(8))
        dewpoint = float(socket.recv(8))
        visib = float(socket.recv(8))
        data = [[wind, dewpoint, visib]]
        probs = clf.predict_proba(data)
        socket.send(probs)
        print "Thread  -> ", wind, " ", dewpoint, " ", visib
        socket.close()
        return

HOST = '';
PORT = 50001
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(5)
classes = [1, 2, 3, 4]
base = np.loadtxt("base.txt", usecols=(0,1,2,3,4))
y_base = base[:,0]
X_base = base[:,1:]
normalize(X_base)

# translate continuous to discrete output for classifier
y_base[y_base < 15] = 1
y_base[np.logical_and(y_base >= 15, y_base < 30)] = 2
y_base[np.logical_and(y_base >= 30, y_base < 60)] = 3
y_base[y_base >= 60] = 4

clf = GaussianNB()
clf.partial_fit(X_base, y_base, classes)

while True:
    # receive data from web app
    (clientsocket, address) = s.accept()
    t = threading.Thread(target=worker, args=(clientsocket,))
    t.start()