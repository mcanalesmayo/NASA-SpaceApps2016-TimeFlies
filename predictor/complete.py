#!usr/bin/python

import numpy as np
from sklearn.naive_bayes import GaussianNB
from sklearn.preprocessing import normalize
import socket
import threading
from sklearn.externals import joblib

def worker(socket):
    command = socket.recv(8)
    if (command == 'FE'): # check value from message
        print "entered"
        # batch training
        num_lines = int(socket.recv(8))
        retraso = float(socket.recv(8))
        wind = float(socket.recv(8))
        dew = float(socket.recv(8))
        cover = float(socket.recv(8))
        batch = np.array([retraso, wind, dew, cover])
        
        i = 1
        while i < num_lines:
            retraso = float(socket.recv(8))
            wind = float(socket.recv(8))
            dew = float(socket.recv(8))
            cover = float(socket.recv(8))
            new_row = np.array([retraso, wind, dew, cover])
            batch = np.vstack([batch, new_row])
            i = i + 1
            
        # reads x, y
        X_batch = batch[:,[1,2,3]]
        y_batch = batch[:,0]
       
        # translate continuous to discrete output for classifier
        y_batch[y_batch <= 5] = 1
        y_batch[np.logical_and(y_batch > 5, y_batch <= 30)] = 2
        y_batch[np.logical_and(y_batch > 30, y_batch <= 60)] = 3
        y_batch[y_batch > 60] = 4
        
        # trains the model
        clf.partial_fit(X_batch, y_batch)
        joblib.dump(clf, 'dump.pkl')
        print "updated"
    elif (command == 'PR'):
        # prediction
        # reads data from client
        wind = float(socket.recv(8))
        dewpoint = float(socket.recv(8))
        skycover = float(socket.recv(8))
        data = np.array([[wind, dewpoint, skycover]])
        
        # predicts and sends an answer
        probs = clf.predict_proba(data)
        probs_sending = " ".join(map(str,probs[0]))
        print probs_sending
        socket.send(probs_sending)
        print "Thread  -> ", wind, " ", dewpoint, " ", skycover
        socket.close()
        return


HOST = '';
PORT = 50001
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(5)
classes = [1, 2, 3, 4]
dump_path = "dump.pkl"

try:
    # Loads dump if exists
    clf = joblib.load(dump_path)
except IOError:
    # No dump, loads base samples
    base = np.loadtxt("base.txt")
    y_base = base[:,0] # col 0: delay in minutes
    X_base = base[:,[1,2,3]] # cols 1(wind spd), 2(dew diff), 3(sky cover)
    normalize(X_base)
    
    # translate continuous to discrete output for classifier
    y_base[y_base <= 5] = 1
    y_base[np.logical_and(y_base > 5, y_base <= 30)] = 2
    y_base[np.logical_and(y_base > 30, y_base <= 60)] = 3
    y_base[y_base > 60] = 4
    
    clf = GaussianNB()
    clf.partial_fit(X_base, y_base, classes)
    joblib.dump(clf, 'dump.pkl')

while True:
    # receive data from web app
    (clientsocket, address) = s.accept()
    t = threading.Thread(target=worker, args=(clientsocket,))
    t.start()