package com.example.sedora.model;

public class Meta {

    private String nombre;
    private String descripcion;
    private int progresoTotal;
    private int progresoActual;
    private String imagenDrawableName; // Cambiado para reflejar recursos locales
    private String numeroMeta;
    private String estado;

    // Constructor vacío
    public Meta() {
    }

    // Constructor con parámetros
    public Meta(String nombre, String descripcion, int progresoTotal, int progresoActual, String imagenDrawableName, String numeroMeta, String estado) {

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.progresoTotal = progresoTotal;
        this.progresoActual = progresoActual;
        this.imagenDrawableName = imagenDrawableName;
        this.numeroMeta = numeroMeta;
        this.estado = estado;
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

    public int getProgresoTotal() {
        return progresoTotal;
    }

    public void setProgresoTotal(int progresoTotal) {
        this.progresoTotal = progresoTotal;
    }

    public int getProgresoActual() {
        return progresoActual;
    }

    public void setProgresoActual(int progresoActual) {
        this.progresoActual = progresoActual;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}