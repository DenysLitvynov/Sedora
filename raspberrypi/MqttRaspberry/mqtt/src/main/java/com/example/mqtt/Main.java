package com.example.mqtt;

public class Main {
    public static void main(String[] args) {
        MqttManager mqttManager = new MqttManager();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> mqttManager.disconnect()));
    }
}

