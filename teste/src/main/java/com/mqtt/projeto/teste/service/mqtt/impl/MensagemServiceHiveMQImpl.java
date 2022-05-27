package com.mqtt.projeto.teste.service.mqtt.impl;

import com.hivemq.client.internal.mqtt.message.publish.MqttPublish;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.mqtt.projeto.teste.config.HiveMQClientFactory;
import com.mqtt.projeto.teste.config.MqttClientFactory;
import com.mqtt.projeto.teste.exception.ConnectionBrokerException;
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

@Profile("HiveMQ")
@Slf4j
@Service
public class MensagemServiceHiveMQImpl implements MensagemService {
    @Autowired
    private HiveMQClientFactory clientFactory;

    @Value("${config.qos}")
    private String qos;

    public void publicarMensagem(MessageRequest messageRequest) {

        try {
            Mqtt3AsyncClient clientHiveMQ = clientFactory.getInstance();
            clientHiveMQ.connect()
                    .whenComplete((connAck, throwable) -> {
                        if (throwable != null) {
                            throw new ConnectionBrokerException("Não foi possivel se conectar ao broker");
                        } else {
                            log.info("Conectado ao broker com sucesso");
                            publicaMensagemNoBroker(messageRequest, clientHiveMQ);
                            log.info("Mensagem publicada com sudcesso");
                        }
                    });
        } catch (Exception e) {
            throw new PublishErrorException("Ocorreu algum erro ao tentar publicar a mensagem no tópico", e);
        }
    }

    private void publicaMensagemNoBroker(MessageRequest messageRequest, Mqtt3AsyncClient clientHiveMQ) {
        String buildMessage = messageRequest.getMessage() + " - origem: " + messageRequest.getOrigin();
        clientHiveMQ.publishWith()
                .topic(messageRequest.getTopico())
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(buildMessage.getBytes())
                .send();
    }


    public List<MessageResponse> verificarMensagemRecebidaPorIntervalo(String topic, Integer waitMillis) throws MqttException, InterruptedException {
        List<MessageResponse> messages = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        try {
            Mqtt3AsyncClient clientHiveMQ = clientFactory.getInstance();
            clientHiveMQ.connect()
                    .whenComplete((connAck, throwable) -> {
                        log.info("conectando");
                        if (throwable != null) {
                            throw new ConnectionBrokerException("Não foi possivel se conectar ao broker");
                        } else {
                            log.info("Conectado ao broker com sucesso");
                            clientHiveMQ.subscribeWith()
                                    .topicFilter(topic)
                                    .qos(MqttQos.AT_LEAST_ONCE)
                                    .callback(mqttMessage -> {
                                        MessageResponse mqttSubscribeModel = new MessageResponse();
                                        mqttSubscribeModel.setMensagem(String.valueOf(mqttMessage.getPayload()));
                                        mqttSubscribeModel.setQos(mqttMessage.getQos().getCode());
                                        messages.add(mqttSubscribeModel);
                                        countDownLatch.countDown();
                                    })
                                    .send();
                            try {
                                countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException e) {
                                throw new ConnectionBrokerException("Conexão interrompida",e);
                            }
                        }
                    });
        } catch (Exception e) {
            throw new PublishErrorException("Ocorreu algum erro ao tentar publicar a mensagem no tópico", e);
        }
        return messages;
    }
}
