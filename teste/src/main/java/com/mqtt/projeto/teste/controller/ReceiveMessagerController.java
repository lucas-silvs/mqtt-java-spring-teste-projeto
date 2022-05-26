package com.mqtt.projeto.teste.controller;

import com.mqtt.projeto.teste.config.MqttClientFactory;
import com.mqtt.projeto.teste.gateway.MqttGateway;
import com.mqtt.projeto.teste.models.MessageRequest;
import com.mqtt.projeto.teste.models.MessageResponse;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/mqtt")
public class ReceiveMessagerController {



    @PostMapping
    public ResponseEntity<Void> postarNoTopicoMqtt(@RequestBody MessageRequest messageRequest){
        try {
            MqttMessage message = new MqttMessage();
            message.setQos(0);
            message.setPayload(messageRequest.getMessage().getBytes());
            System.out.println(messageRequest.getMessage());
            MqttClientFactory.getInstance().publish("topicoExemplo", message);
            return ResponseEntity.ok().build();
        }catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("subscribe")
    public List<MessageResponse> subscribeChannel(@RequestParam(value = "topico") String topic,
                                                  @RequestParam(value = "timerEmMilisegundos") Integer waitMillis)
            throws InterruptedException, org.eclipse.paho.client.mqttv3.MqttException {
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
