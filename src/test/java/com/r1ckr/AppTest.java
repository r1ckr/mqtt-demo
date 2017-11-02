package com.r1ckr;

import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class AppTest
{

    private Server server = new Server();
    /**
     * Rigourous Test :-)
     */
    @Before
    public void setup() throws IOException {
        Properties properties = new Properties();
        properties.setProperty(BrokerConstants.PORT_PROPERTY_NAME, "31883");
        server.startServer(properties);
    }

    @Test
    public void testApp() throws MqttException, InterruptedException {

        String broker = "tcp://localhost:31883";

        // Subscriber to listen all the messages under home
        MqttClient superSubscriber=new MqttClient(broker, MqttClient.generateClientId());
        SimpleMqttCallBack superSubscriberCallback = new SimpleMqttCallBack("Super Subscriber");
        superSubscriber.setCallback(superSubscriberCallback);
        superSubscriber.connect();
        superSubscriber.subscribe("home/#");

        // Subscriber to listen for messages under home/temperature
        MqttClient temperatureSubscriber=new MqttClient(broker, MqttClient.generateClientId());
        SimpleMqttCallBack temperatureSubscriberCallback = new SimpleMqttCallBack("Temperature Subscriber");
        temperatureSubscriber.setCallback(temperatureSubscriberCallback);
        temperatureSubscriber.connect();
        temperatureSubscriber.subscribe("home/temperature");

        // Subscriber to listen for messages under home/security
        MqttClient securitySubscriber=new MqttClient(broker, MqttClient.generateClientId());
        SimpleMqttCallBack securitySubscriberCallback = new SimpleMqttCallBack("Security Subscriber");
        securitySubscriber.setCallback(securitySubscriberCallback);
        securitySubscriber.connect();
        securitySubscriber.subscribe("home/security");

        MqttClient publisher = new MqttClient(broker, MqttClient.generateClientId());
        publisher.connect();
        MqttMessage message = new MqttMessage();

        message.setPayload("Welcome home".getBytes());
        publisher.publish("home", message);

        message.setPayload("Temperature is going high!".getBytes());
        publisher.publish("home/temperature", message);

        message.setPayload("Security, the cat has opened the window".getBytes());
        publisher.publish("home/security", message);

        publisher.disconnect();
        superSubscriber.disconnect();
        temperatureSubscriber.disconnect();
        securitySubscriber.disconnect();

        assertEquals(3, superSubscriberCallback.getMessages().size());
        assertEquals(1, securitySubscriberCallback.getMessages().size());
        assertEquals(1, temperatureSubscriberCallback.getMessages().size());

    }

    public class SimpleMqttCallBack implements MqttCallback {

        private String id;
        private List<String> messages = new ArrayList<String>();

        public SimpleMqttCallBack(String id){
            this.id = id;
        }

        public void connectionLost(Throwable throwable) {
            System.out.println("Connection to MQTT broker lost!");
        }

        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            String payload = new String(mqttMessage.getPayload());
            System.out.println("Message received by \""+id+"\":\n\t"+ payload );
            messages.add(payload);
        }

        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            // not used in this example
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    @After
    public void shutdown(){
        server.stopServer();
    }
}
