package com.mqtt.projeto.teste.service.mqtt.impl;

import com.mqtt.projeto.teste.config.MqttClientFactory;
import com.mqtt.projeto.teste.models.MessageRequest;
import com.mqtt.projeto.teste.models.MessageResponse;
import com.mqtt.projeto.teste.service.mqtt.MensagemService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class MensagemServiceImpl implements MensagemService {


    public void publicarMensagem(MessageRequest messageRequest) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setQos(0);
        message.setPayload(messageRequest.getMessage().getBytes());
        System.out.println(messageRequest.getMessage());
        MqttClientFactory.getInstance().publish(messageRequest.getTopico(), message);

    }

    @Override
    public List<MessageResponse> verificarMensagemRecebidaPorIntervalo(String topic, Integer waitMillis) throws MqttException, InterruptedException {
        List<MessageResponse> messages = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        MqttClientFactory.getInstance().subscribeWithResponse(topic, (s, mqttMessage) -> {
            MessageResponse mqttSubscribeModel = new MessageResponse();
            mqttSubscribeModel.setId(mqttMessage.getId());
            mqttSubscribeModel.setMensagem(new String(mqttMessage.getPayload()));
            mqttSubscribeModel.setQos(mqttMessage.getQos());
            messages.add(mqttSubscribeModel);
            countDownLatch.countDown();
        });

        countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);
        return messages;
    }
}
