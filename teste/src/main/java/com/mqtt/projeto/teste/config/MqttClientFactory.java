package com.mqtt.projeto.teste.config;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class MqttClientFactory {
    @Value("${mqtt.topic.id}")
    private String mqttPublisherId;
    @Value("${mqtt.topic.uri}")
    private String mqttServerAddress;
    private IMqttClient instance;

    public  IMqttClient getInstance() {
        try {
            if (instance == null) {
                instance = new MqttClient(mqttServerAddress, mqttPublisherId);
            }

            MqttConnectOptions options = generateMqttOptions();

            if (!instance.isConnected()) {
                instance.connect(options);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private static MqttConnectOptions generateMqttOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        return options;
    }

}
