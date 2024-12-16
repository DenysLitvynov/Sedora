package com.example.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttManager implements MqttCallback {
    private static final String topic = "Sedora/#";
    private static final int qos = 1;
    private static final String broker = "tcp://broker.hivemq.com:1883";
    private static final String clientId = "hugoapp" + System.currentTimeMillis();
    private MqttClient client;

    public MqttManager() {
        try {
            client = new MqttClient(broker, clientId, new MemoryPersistence());

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill("Sedora/desconectado", "Desconectada!".getBytes(), 0, false); // Mensaje de última voluntad

            client.setCallback(this); // Asigna esta clase como callback
            client.connect(connOpts);

            // Suscripción a los tópicos
            client.subscribe("Sedora/sensores/temperatura", qos);
            client.subscribe("Sedora/sensores/humedad", qos);
            client.subscribe("Sedora/sensores/luminosidad", qos);
            client.subscribe("Sedora/sensores/presion1", qos);
            client.subscribe("Sedora/sensores/presion2", qos);
            client.subscribe("Sedora/sensores/proximidad", qos);
            client.subscribe("Sedora/sensores/ruido", qos);
            client.subscribe("Sedora/sensores/timestamp", qos);
            System.out.println("Suscripción a los tópicos de sensores completada.");

        } catch (MqttException e) {
            System.err.println("Error al conectar con el bróker: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                System.out.println("Cliente desconectado");
            }
        } catch (MqttException e) {
            System.err.println("Error al desconectar el cliente: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Conexión perdida...");
        if (cause != null) {
            cause.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("Mensaje recibido en el tópico: " + topic + " - Payload: " + payload);

        // Procesamiento según el tópico
        try {
            switch (topic) {
                case "Sedora/sensores/temperatura":
                    System.out.println("temperatura: " + payload); // Imprime el valor de temperatura
                    break;
                case "Sedora/sensores/humedad":
                    System.out.println("humedad: " + payload); // Imprime el valor de humedad
                    break;
                case "Sedora/sensores/luminosidad":
                    System.out.println("luminosidad: " + payload); // Imprime el valor de luminosidad
                    break;
                case "Sedora/sensores/presion1":
                    System.out.println("presion1: " + payload); // Imprime el valor de presion1
                    break;
                case "Sedora/sensores/presion2":
                    System.out.println("presion2: " + payload); // Imprime el valor de presion2
                    break;
                case "Sedora/sensores/proximidad":
                    System.out.println("proximidad: " + payload); // Imprime el valor de proximidad
                    break;
                case "Sedora/sensores/ruido":
                    System.out.println("ruido: " + payload); // Imprime el valor de ruido
                    break;
                case "Sedora/sensores/timestamp":
                    System.out.println("timestamp: " + payload); // Imprime la marca de tiempo
                    break;
                default:
                    System.out.println("Dato desconocido recibido del tópico: " + topic);
            }
        } catch (Exception e) {
            System.err.println("Error al procesar el mensaje: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Entrega completa!");
    }
}