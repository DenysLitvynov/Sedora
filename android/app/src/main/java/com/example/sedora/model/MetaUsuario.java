package com.example.sedora.model;

public class MetaUsuario {

    private Meta meta;
    private int porcentajeActual;

    private EstadoMeta estadoMeta;


    public MetaUsuario() {
    }

    public MetaUsuario(Meta meta) {
        this.meta = meta;
        porcentajeActual = 0;
        estadoMeta = EstadoMeta.PENDIENTE;

        if(meta.getNumeroMeta().equals("01")){
            estadoMeta = EstadoMeta.ACTUAL;
        }
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public EstadoMeta getEstadoMeta() {
        return estadoMeta;
    }

    public void setEstadoMeta(EstadoMeta estadoMeta) {
        this.estadoMeta = estadoMeta;
    }

    public int getPorcentajeActual() {
        return porcentajeActual;
    }

    public void setPorcentajeActual(int porcentajeActual) {
        this.porcentajeActual = porcentajeActual;
    }
}