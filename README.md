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

## Resources
* Python
* JavaEE
* Spring Framework
* Gradle
* Forecast.io
* NOAA (METAR)
* National Weather Service
* HTML5
* CSS3
* Bootstrap
* JavaScript
* Scikit Learn - Naive Bayes Clasification Multiclass


## Server side
The server side has been built using the following technologies:
* Web Server: JavaEE & Spring Framework
* Predictor: Python & Scikit-Learn
* Communication between web server and predictor: sockets

## Client interface
The client interface has been build using the following technologies:
* HTML5
* CSS3
* Bootstrap
* Javascript & JQuery

## Slides
http://es.slideshare.net/JorgeCncerGil/timeflies-spaceapps-nasa-zaragoza


## Notes
The python project has been included in this project. It's the _predictor_ folder

Help/Examples:
* https://www.aviationweather.gov/adds/metars
* https://www.aviationweather.gov/static/adds/metars/stations.txt
* https://www.aviationweather.gov/dataserver
* https://en.wikipedia.org/wiki/METAR
* https://www.aviationweather.gov/adds/metars?station_ids=KAWO+KBLI&std_trans=standard&chk_metars=on&hoursStr=most+recent+only&submitmet=Submit
* https://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&startTime=2016-04-17T10:37:22+0700&endTime=2016-04-17T12:37:22+0700&stationString=PHTO

* https://www.faasafety.gov/files/gslac/courses/content/25/185/VFR%20Weather%20Minimums.pdf
