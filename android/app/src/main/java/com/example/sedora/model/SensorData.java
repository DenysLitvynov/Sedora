package com.example.sedora.model;

import com.google.firebase.Timestamp;

public class SensorData {
    private double luminosidad;
    private double presion1;
    private double presion2;
    private double ruido;
    private double temperatura;
    private double humedad;
    private double proximidad;
    private double postura;
    private double distanciaMonitor;
    private double iluminacion;
    private double tiempoSentado;
    private double hidratacion;
    private Timestamp timestamp;

    public SensorData() {}

    public SensorData(double luminosidad, double presion1, double presion2,
                      double ruido, double temperatura, double humedad, double proximidad,
                      double postura, double distanciaMonitor, double iluminacion,
                      double tiempoSentado, double hidratacion, Timestamp timestamp) {
        this.luminosidad = luminosidad;
        this.presion1 = presion1;
        this.presion2 = presion2;
        this.ruido = ruido;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.proximidad = proximidad;
        this.postura = postura;
        this.distanciaMonitor = distanciaMonitor;
        this.iluminacion = iluminacion;
        this.tiempoSentado = tiempoSentado;
        this.hidratacion = hidratacion;
        this.timestamp = timestamp;
    }

    // Getters y Setters para todos los atributos
    public double getLuminosidad() { return luminosidad; }
    public void setLuminosidad(double luminosidad) { this.luminosidad = luminosidad; }

    public double getPresion1() { return presion1; }
    public void setPresion1(double presion1) { this.presion1 = presion1; }

    public double getPresion2() { return presion2; }
    public void setPresion2(double presion2) { this.presion2 = presion2; }

    public double getRuido() { return ruido; }
    public void setRuido(double ruido) { this.ruido = ruido; }

    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }

    public double getHumedad() { return humedad; }
    public void setHumedad(double humedad) { this.humedad = humedad; }

    public double getProximidad() { return proximidad; }
    public void setProximidad(double proximidad) { this.proximidad = proximidad; }

    public double getPostura() { return postura; }
    public void setPostura(double postura) { this.postura = postura; }

    public double getDistanciaMonitor() { return distanciaMonitor; }
    public void setDistanciaMonitor(double distanciaMonitor) { this.distanciaMonitor = distanciaMonitor; }

    public double getIluminacion() { return iluminacion; }
    public void setIluminacion(double iluminacion) { this.iluminacion = iluminacion; }

    public double getTiempoSentado() { return tiempoSentado; }
    public void setTiempoSentado(double tiempoSentado) { this.tiempoSentado = tiempoSentado; }

    public double getHidratacion() { return hidratacion; }
    public void setHidratacion(double hidratacion) { this.hidratacion = hidratacion; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
