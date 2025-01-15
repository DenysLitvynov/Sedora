package com.example.sedora.model;

public class Meta {

    private String nombre;
    private String descripcion;
    private String imagenDrawableName;
    private String numeroMeta;
    private int porcentaje;

    public Meta() {
    }

    public Meta(String nombre, String descripcion, String imagenDrawableName, String numeroMeta) {

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenDrawableName = imagenDrawableName;
        this.numeroMeta = numeroMeta;
        this.porcentaje = 100;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenDrawableName() {
        return imagenDrawableName;
    }

    public void setImagenDrawableName(String imagenDrawableName) {
        this.imagenDrawableName = imagenDrawableName;
    }

    public String getNumeroMeta() {
        return numeroMeta;
    }

    public void setNumeroMeta(String numeroMeta) {
        this.numeroMeta = numeroMeta;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

}