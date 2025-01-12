package com.example.sedora.model;

import com.google.firebase.Timestamp;

public class SensorMediaData {

    private Timestamp fecha;
    private double humedadPromedio;
    private double luminosidadPromedio;
    private double presion1Promedio;
    private double presion2Promedio;
    private double proximidadPromedio;
    private double ruidoPromedio;
    private double temperaturaPromedio;


    public SensorMediaData() {}

    public SensorMediaData(Timestamp fecha, double humedadPromedio, double luminosidadPromedio, double presion1Promedio, double presion2Promedio, double proximidadPromedio, double ruidoPromedio, double temperaturaPromedio) {
        this.fecha = fecha;
        this.humedadPromedio = humedadPromedio;
        this.luminosidadPromedio = luminosidadPromedio;
        this.presion1Promedio = presion1Promedio;
        this.presion2Promedio = presion2Promedio;
        this.proximidadPromedio = proximidadPromedio;
        this.ruidoPromedio = ruidoPromedio;
        this.temperaturaPromedio = temperaturaPromedio;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public double getHumedadPromedio() {
        return humedadPromedio;
    }

    public void setHumedadPromedio(double humedadPromedio) {
        this.humedadPromedio = humedadPromedio;
    }

    public double getLuminosidadPromedio() {
        return luminosidadPromedio;
    }

    public void setLuminosidadPromedio(double luminosidadPromedio) {
        this.luminosidadPromedio = luminosidadPromedio;
    }

    public double getPresion1Promedio() {
        return presion1Promedio;
    }

    public void setPresion1Promedio(double presion1Promedio) {
        this.presion1Promedio = presion1Promedio;
    }

    public double getPresion2Promedio() {
        return presion2Promedio;
    }

    public void setPresion2Promedio(double presion2Promedio) {
        this.presion2Promedio = presion2Promedio;
    }

    public double getProximidadPromedio() {
        return proximidadPromedio;
    }

    public void setProximidadPromedio(double proximidadPromedio) {
        this.proximidadPromedio = proximidadPromedio;
    }

    public double getRuidoPromedio() {
        return ruidoPromedio;
    }

    public void setRuidoPromedio(double ruidoPromedio) {
        this.ruidoPromedio = ruidoPromedio;
    }

    public double getTemperaturaPromedio() {
        return temperaturaPromedio;
    }

    public void setTemperaturaPromedio(double temperaturaPromedio) {
        this.temperaturaPromedio = temperaturaPromedio;
    }
}
