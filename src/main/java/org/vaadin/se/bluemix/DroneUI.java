package org.vaadin.se.bluemix;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.vaadin.mqtt.ui.MqttDashboard;
import org.vaadin.mqtt.ui.MqttDashboardUI;
import org.vaadin.mqtt.ui.MqttDataSource;
import org.vaadin.mqtt.ui.MqttMessageConverter;
import org.vaadin.mqtt.ui.MqttTopic;
import org.vaadin.mqtt.ui.converters.DoubleConverter;
import org.vaadin.mqtt.ui.converters.ListConverter;
import org.vaadin.mqtt.ui.displays.GaugeDisplay;
import org.vaadin.mqtt.ui.displays.ListDisplay;
import org.vaadin.mqtt.ui.inputs.SliderInput;

/**
 * Specialized MQTT Dashboard for TinkerForge sensors.
 *
 * @author Sami Ekblad
 */
public class DroneUI extends MqttDashboardUI {

    /**
     * Servlet bootstrap.
     *
     * @author Sami Ekblad
     */
    @WebServlet(value = {"/drone/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DroneUI.class)
    public static class Servlet extends VaadinServlet {
    }

    private static final Properties PROPERTIES = new Properties();
    static {
        try {
            PROPERTIES.load(DroneUI.class.getResourceAsStream("/iot-foundation.properties"));
        } catch (IOException ex) {
            Logger.getLogger(DroneUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static final String IOT_ORG = PROPERTIES.getProperty("org");
    private static final String IOT_TYPE = PROPERTIES.getProperty("type");
    private static final String IOT_ID = PROPERTIES.getProperty("id");
    private static final String MQTT_BROKER = "tcp://" + IOT_ORG + ".messaging.internetofthings.ibmcloud.com:1883";

    private static final String TX_CLIENT_ID = "d:" + IOT_ORG + ":" + IOT_TYPE + ":" + IOT_ID;
    private static final String TX_USER_NAME = "use-token-auth";
    private static final String TX_PASSWORD = PROPERTIES.getProperty("auth-token");

    private static final String RX_CLIENT_ID = "a:" + IOT_ORG + ":";
    private static final String RX_USER_NAME = PROPERTIES.getProperty("app-apikey");
    private static final String RX_PASSWORD = PROPERTIES.getProperty("app-auth-token");

    private static final MqttTopic ALTITUDE_TX = new MqttTopic("iot-2/evt/vaadindrone-data/fmt/json", "Set altitude", "m", 0, 8);
    private static final MqttTopic ALTITUDE_RX = new MqttTopic("iot-2/type/+/id/+/evt/+/fmt/+", "Altitude", "m", 0, 8);
    private static final MqttTopic MESSAGES_RX = new MqttTopic("iot-2/type/+/id/+/evt/+/fmt/+", "Messages");

    // Special data converters for the above messages
    private MqttMessageConverter VALUE_CONVERTER = new DoubleConverter();
    private MqttMessageConverter LIST_CONVERTER = new ListConverter();

    // Dashboard specification
    private final MqttDashboard dashboardSpec = new MqttDashboard("Drone") {
        {
            MqttConnectOptions recvOpts = new MqttConnectOptions();
            recvOpts.setUserName(RX_USER_NAME);
            recvOpts.setPassword(RX_PASSWORD.toCharArray());
            add(ListDisplay.class, new MqttDataSource(MQTT_BROKER, RX_CLIENT_ID + "1" + System.currentTimeMillis(), recvOpts, MESSAGES_RX), LIST_CONVERTER);
            add(GaugeDisplay.class, new MqttDataSource(MQTT_BROKER, RX_CLIENT_ID + "2" + System.currentTimeMillis(), recvOpts, ALTITUDE_RX), VALUE_CONVERTER, "#0000BB", "#BBBBFF", "#BBBBFF", "#FF0000");

            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setUserName(TX_USER_NAME);
            opts.setPassword(TX_PASSWORD.toCharArray());
            add(SliderInput.class, new MqttDataSource(MQTT_BROKER, TX_CLIENT_ID, opts, ALTITUDE_TX), null);
        }

    };

    @Override
    public MqttDashboard getDashboardSpec() {
        return this.dashboardSpec;
    }

}
