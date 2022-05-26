package com.mqtt.projeto.teste.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageRequest{
    @JsonProperty("origin")
    private String origin;

    @JsonProperty("message")
    private String Message;

    @JsonProperty("topico")
    private String topico;
}
