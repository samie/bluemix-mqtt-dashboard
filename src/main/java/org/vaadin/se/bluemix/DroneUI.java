package org.vaadin.se.bluemix;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
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

    private static final String MQTT_BROKER = "tcp://kwxdxh.messaging.internetofthings.ibmcloud.com:1883";

    private static final String MQTT_TX_CLIENT_ID = "d:kwxdxh:<appname>:<username>";
    private static final String MQTT_TX_USER_NAME = "use-token-auth";
    private static final String MQTT_TX_PASSWORD = "<sender password>";

    private static final String MQTT_RX_CLIENT_ID = "a:kwxdxh:";
    private static final String MQTT_RX_USER_NAME = "<receiver username>";
    private static final String MQTT_RX_PASSWORD = "<receiver password>";

    protected static final String MQTT_MESSAGE_CHARSET = "UTF-8";

    // Messages published by the TinkerFoge Wetterstation
    private static final MqttTopic ALTITUDE_TX = new MqttTopic("iot-2/evt/vaadindrone-data/fmt/json", "Set altitude", "m", 0, 8);
    private static final MqttTopic ALTITUDE_RX = new MqttTopic("iot-2/type/+/id/+/evt/+/fmt/+", "Altitude", "m", 0, 8);
    private static final MqttTopic ALL = new MqttTopic("iot-2/type/+/id/+/evt/+/fmt/+", "Messages");

    // Special data parsers for the above messages
    private MqttMessageConverter VALUE_CONVERTER = new DoubleConverter();
    private MqttMessageConverter LIST_CONVERTER = new ListConverter();

    // Dashboard specification
    private final MqttDashboard dashboardSpec = new MqttDashboard("Drone") {
        {
            MqttConnectOptions recvOpts = new MqttConnectOptions();
            recvOpts.setUserName(MQTT_RX_USER_NAME);
            recvOpts.setPassword(MQTT_RX_PASSWORD.toCharArray());
            add(ListDisplay.class, new MqttDataSource(MQTT_BROKER, MQTT_RX_CLIENT_ID+ System.currentTimeMillis(), recvOpts, ALTITUDE_RX), LIST_CONVERTER);
            add(GaugeDisplay.class, new MqttDataSource(MQTT_BROKER, MQTT_RX_CLIENT_ID+ System.currentTimeMillis(), recvOpts, ALTITUDE_RX), VALUE_CONVERTER, "#0000BB", "#BBBBFF", "#BBBBFF", "#FF0000");

            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setUserName(MQTT_TX_USER_NAME);
            opts.setPassword(MQTT_TX_PASSWORD.toCharArray());
            add(SliderInput.class, new MqttDataSource(MQTT_BROKER, MQTT_TX_CLIENT_ID, opts, ALTITUDE_TX), null);
        }

    };

    @Override
    public MqttDashboard getDashboardSpec() {
        return this.dashboardSpec;
    }

}
