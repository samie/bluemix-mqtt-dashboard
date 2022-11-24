# Vaadin MQTT Dashboard sample for Bluemix

There are several  displays for [MQTT](http://mqtt.org/) based [Bluemix IoT](https://internetofthings.ibmcloud.com/) dashboards:
 * round gauge with text value 
 * vertical bar display  
 * sparkline for history data
 * polar display for directional data
 * list display for text messages

By default the application shows drone altitude, message log and slider to set the drone altitude.

![Screenshot](/drone-altitude-dashboard.png?raw=true "Drone control")

You can find test it out live here: [mqtt-dashboard.mybluemix.net/drone](http://mqtt-dashboard.mybluemix.net/drone)

## Get the source and compile

To compile the the application locally, use:

     git clone https://github.com/samie/bluemix-mqtt-dashboard.git
     cd bluemix-mqtt-dashboard
     mvn install

_NOTE: the application does not work out-of-the-box and you need to configure the IoT foundation first._ 

## Setting up IoT foundation for messaging 

First you need to [setup the The IBM Internet of Things service](https://www.ng.bluemix.net/docs/#services/IoT/index.html#gettingstartedtemplate)
in your Bluemix dashboard and configure the application. 
![Screenshot](/setup-iot-service.png?raw=true "Setup IoT in Bluemix")

You can find the application configuration in the [iot-foundation.properties](src/main/resources/iot-foundation.properties). Copy the required configuration data from the dashboard "Devices" and "API keys" tabs.

Note that, if you add more displays in your code, a different client id is needed for every connecting client.

## Running the app

To run the application locally and open browser:

     mvn jetty:run
     open http://localhost:8080/drone

Deploy to Bluemix using CloudFoundry commandline tool:

     cf push <your-app-name> -p target/bluemix-mqtt-dashboard-1.0-SNAPSHOT.war


And your application is online at &lt;your-app-name&gt;.mybluemix.net/drone



----
_Disclaimer: This source code is provided as a sample only. The application is provided "as-is," without any warranty. In no event shall the author be held liable for any costs or damages arising from the use of the software._
