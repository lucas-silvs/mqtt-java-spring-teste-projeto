package com.mqtt.projeto.teste.exception;

public class PublishErrorException extends RuntimeException{

    public PublishErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
