package com.mqtt.projeto.teste.models;

import lombok.Data;

@Data
public class MessageResponse {

    private String mensagem;
    private Integer id;
    private Integer qos;


}
