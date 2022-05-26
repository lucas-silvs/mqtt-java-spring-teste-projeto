package com.mqtt.projeto.teste.service.mqtt.impl;

import com.mqtt.projeto.teste.config.MqttClientFactory;
import com.mqtt.projeto.teste.exception.PublishErrorException;
import com.mqtt.projeto.teste.models.MessageRequest;
import com.mqtt.projeto.teste.models.MessageResponse;
import com.mqtt.projeto.teste.service.mqtt.MensagemService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MensagemServiceImpl implements MensagemService {
    @Autowired
    private MqttClientFactory clientFactory;


    public void publicarMensagem(MessageRequest messageRequest){

        MqttMessage message = new MqttMessage();
        message.setQos(0);
        message.setPayload(messageRequest.getMessage().getBytes());
        try {
            clientFactory.getInstance().publish(messageRequest.getTopico(), message);
            log.info("Mensagem publicada com sudcesso");
        } catch (MqttException e) {
            throw new PublishErrorException("Ocorreu algum erro ao tentar publicar a mensagem no tópico",e);
        }


    }

    @Override
    public List<MessageResponse> verificarMensagemRecebidaPorIntervalo(String topic, Integer waitMillis) throws MqttException, InterruptedException {
        List<MessageResponse> messages = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        clientFactory.getInstance().subscribeWithResponse(topic, (s, mqttMessage) -> {
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
