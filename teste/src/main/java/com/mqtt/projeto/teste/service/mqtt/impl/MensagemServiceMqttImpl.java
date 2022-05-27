package com.mqtt.projeto.teste.service.mqtt.impl;

import com.mqtt.projeto.teste.config.MqttClientFactory;
import com.mqtt.projeto.teste.exception.PublishErrorException;
import com.mqtt.projeto.teste.models.MessageRequest;
import com.mqtt.projeto.teste.models.MessageResponse;
import com.mqtt.projeto.teste.service.mqtt.MensagemService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Profile("local")
@Slf4j
@Service
public class MensagemServiceMqttImpl implements MensagemService {
    @Autowired
    private MqttClientFactory clientFactory;

    @Value("${config.qos}")
    private int qos;

    public void publicarMensagem(MessageRequest messageRequest){
        String buildMessage = messageRequest.getMessage() +" - origem: "+ messageRequest.getOrigin();
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setPayload(buildMessage.getBytes());
        try {
            clientFactory.getInstance().publish(messageRequest.getTopico(), message);
            log.info("Mensagem publicada com sudcesso");
        } catch (MqttException e) {
            throw new PublishErrorException("Ocorreu algum erro ao tentar publicar a mensagem no tópico",e);
        }
    }


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
