package com.mqtt.projeto.teste.controller;

import com.mqtt.projeto.teste.models.MessageRequest;
import com.mqtt.projeto.teste.models.MessageResponse;
import com.mqtt.projeto.teste.service.mqtt.MensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/mqtt")
public class ReceiveMessagerController {

    @Autowired
    private MensagemService mensagemService;



    @PostMapping
    public ResponseEntity<Void> postarNoTopicoMqtt(@RequestBody MessageRequest messageRequest){
        try {
            mensagemService.publicarMensagem(messageRequest);
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
        List<MessageResponse> messages = mensagemService.verificarMensagemRecebidaPorIntervalo(topic, waitMillis);
        return messages;
    }


}
