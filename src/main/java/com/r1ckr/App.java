package com.r1ckr;

import org.eclipse.paho.client.mqttv3.*;

public class App 
{
    public static void main( String[] args ) throws MqttException, InterruptedException {

        String broker = "tcp://localhost:1883";

        // Subscriber to listen all the messages under home
        MqttClient superSubscriber=new MqttClient(broker, MqttClient.generateClientId());
        superSubscriber.setCallback( new SimpleMqttCallBack("Super Subscriber"));
        superSubscriber.connect();
        superSubscriber.subscribe("home/#");

        // Subscriber to listen for messages under home/temperature
        MqttClient temperatureSubscriber=new MqttClient(broker, MqttClient.generateClientId());
        temperatureSubscriber.setCallback( new SimpleMqttCallBack("Temperature Subscriber"));
        temperatureSubscriber.connect();
        temperatureSubscriber.subscribe("home/temperature");

        // Subscriber to listen for messages under home/security
        MqttClient securitySubscriber=new MqttClient(broker, MqttClient.generateClientId());
        securitySubscriber.setCallback( new SimpleMqttCallBack("Security Subscriber"));
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

    }

    public static class SimpleMqttCallBack implements MqttCallback {

        private String id;

        public SimpleMqttCallBack(String id){
            this.id = id;
        }

        public void connectionLost(Throwable throwable) {
            System.out.println("Connection to MQTT broker lost!");
        }

        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            System.out.println("Message received by \""+id+"\":\n\t"+ new String(mqttMessage.getPayload()) );
        }

        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            // not used in this example
        }
    }
}
