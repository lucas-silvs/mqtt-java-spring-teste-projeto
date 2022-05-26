package com.mqtt.projeto.teste.gateway;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway(defaultRequestChannel = "mqttOutBoundChannel")
public interface MqttGateway {

    void senToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}
