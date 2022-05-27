package com.mqtt.projeto.teste.exception;

public class ConnectionBrokerException extends RuntimeException{

    public ConnectionBrokerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionBrokerException(String message) {
        super(message);
    }
}
