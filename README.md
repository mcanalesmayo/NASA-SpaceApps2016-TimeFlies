# NASA-SA-ClearForTakeOff

## Developers
* [Íñigo Alonso Ruiz](https://github.com/Shathe)
* [Javier Beltrán Jorba](https://github.com/MrJavo94)
* [Marcos Canales Mayo](https://github.com/MarcosCM) 
* [Jorge Cáncer Gil](https://github.com/jorcox)
* [Jorge Martínez Lascorz](https://github.com/JorgeCoke)

## Description
Our application predicts the likelihood of flight delays given the airport location, departure date and departure hour. Our system has two main nodes. We have a web server deployed using Spring Framework with a responsive design suitable for mobile devices. The magic happens in a Python calculation server implementing machine learning algorithms, which is able to estimate the probabilities of delaying.

The application is constantly taking data from the internet and updating the bayesian model in order to keep learning forever.

Please, check out our [slides](http://es.slideshare.net/JorgeCncerGil/timeflies-spaceapps-nasa-zaragoza).

You can also use the application following this [link](http://52.28.76.182:8080/).

## Resources
* Forecast.io
* NOAA
* National Weather Service
* Flightview tracker

## Server side
The server side has been built using the following technologies:
* Web Server: JavaEE, Spring Framework & Gradle
* Predictor: Python & Scikit-Learn - Naive Bayes Multiclass Clasification
* Communication between web server and predictor: sockets

## Client interface
The client interface has been build using the following technologies:
* HTML5
* CSS3
* Bootstrap
* Javascript & JQuery

## Notes
The python project has been included in the [predictor](predictor) folder of this project.

## Deployment instructions
The server needs the following tools to be run:
* Java 8
* Gradle >=2.12
* Python >=2.7.9
* Scikit-Learn 0.17.1
* python-numpy
* python-scipy

The following commands will run the application:
```
git clone https://github.com/MarcosCM/NASA-SpaceApps2016-TimeFlies.git
cd predictor
nohup python complete.py >/dev/null 2>&1 &
cd ..
nohup gradle run >/dev/null 2>&1 &
```
The web server will now be listening on port 8080.
