package com.mqtt.projeto.teste.service.mqtt;

import com.mqtt.projeto.teste.models.MessageRequest;
import com.mqtt.projeto.teste.models.MessageResponse;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;

public interface MensagemService {

    void publicarMensagem(MessageRequest messageRequest) throws MqttException;

    List<MessageResponse> verificarMensagemRecebidaPorIntervalo(String topic, Integer waitMillis) throws MqttException, InterruptedException;

}
