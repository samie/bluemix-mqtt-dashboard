# mqtt-dashboard
MQTT Dashboard sample for Bluemix

There are several dashboard gauge available
 * round gauge with value 
 * vertical bar gauge 
 * sparkline for history data
 * polar display for directional data
 * list display for text messages

By default the application shows drone altitude, message log and slider to set drone altitude.

To compile the the application locally, use:

     git clone https://github.com/samie/bluemix-mqtt-dashboard.git
     cd bluemix-mqtt-dashboard
     mvn install

To connect the application to Bluemix IoT service you need to [sign up for IoT Foundation](https://www-968.ibm.com/marketplace/cloud/buy-internet-of-things-foundation/us/en-us)
and configure the application. You can find the necessary configuration in the [DroneUI.java](src/main/java/org/vaadin/se/bluemix/DroneUI.java)

    // Sending client
    String MQTT_TX_CLIENT_ID = "d:kwxdxh:<appname>:<username>";
    String MQTT_TX_USER_NAME = "use-token-auth";
    String MQTT_TX_PASSWORD = "<sender password>";

    // Receiving client
    String MQTT_RX_CLIENT_ID = "a:kwxdxh:";
    String MQTT_RX_USER_NAME = "<receiver username>";
    String MQTT_RX_PASSWORD = "<receiver password>";

These must be set in order the dashboard to connect to messaging service. Note that different client id is needed for every connecting client.

To run the application locally and open browser:

     mvn jetty:run
     open http://localhost:8080/drone

Deploy to Bluemix:

     cf push <your-app-name> -p target/mqtt-dashboard-1.0-SNAPSHOT.war
