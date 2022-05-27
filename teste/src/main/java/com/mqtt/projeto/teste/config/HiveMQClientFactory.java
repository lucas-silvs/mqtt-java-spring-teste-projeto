package com.mqtt.projeto.teste.config;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.ConnectException;
import java.util.UUID;

@Data
@Configuration
public class HiveMQClientFactory {

    @Value("${config.topic.id}")
    private String mqttPublisherId;
    @Value("${config.topic.uri}")
    private String mqttServerAddress;

    @Value("${config.credential.username}")
    private String userName;

    @Value("${config.credential.password}")
    private String password;

    private Mqtt3AsyncClient mqtt3AsyncClient;


    public Mqtt3AsyncClient getInstance(){
        Mqtt3AsyncClient client = null;
        if(userName.isEmpty() && password.isEmpty()) {
             client = MqttClient.builder()
                    .useMqttVersion3()
                    .identifier(mqttPublisherId)
                    .serverHost("broker.hivemq.com")
                    .serverPort(1883)
                    .buildAsync();
        }else {
             client = MqttClient.builder()
                    .useMqttVersion3()
                    .simpleAuth()
                    .username(userName)
                    .password(password.getBytes())
                    .applySimpleAuth()
                    .identifier(mqttPublisherId)
                    .serverHost("broker.hivemq.com")
                    .serverPort(1883)
                    .buildAsync();



        }

        return client;

    }


}
