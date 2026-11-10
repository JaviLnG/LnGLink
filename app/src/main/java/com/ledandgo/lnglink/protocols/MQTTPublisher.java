package com.ledandgo.lnglink.protocols;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

public class MQTTPublisher {
    private MqttClient client;

    public synchronized void connect(
            String brokerURL, String clientID, String username, String password)
            throws MqttException {

        if (client != null && client.isConnected()) return;

        client = new MqttClient(brokerURL, clientID, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);

        options.setUserName(username);
        options.setPassword(password.toCharArray());

        client.connect(options);
    }

    public synchronized void publish(String topic, String data) throws MqttException {
        if (client == null || !client.isConnected())
            throw new IllegalStateException("MQTT no está conectado");

        MqttMessage message = new MqttMessage(data.getBytes(StandardCharsets.UTF_8));

        message.setQos(1);
        message.setRetained(false);

        client.publish(topic, message);
    }

    public boolean isConnected() { return client != null && client.isConnected(); }

    public synchronized void disconnect() throws MqttException {
        if (client != null && client.isConnected()) client.disconnect();
    }
}
