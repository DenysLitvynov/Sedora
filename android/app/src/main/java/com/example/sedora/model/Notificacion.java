package com.example.sedora.model;

public class Notificacion {

    private String titulo;
    private String mensaje;
    private String tipo;
    private String hora;
    private int numeroAvisos;
    private int icono;

    // Constructor sin argumentos (necesario para Firestore)
    public Notificacion() { }

    // Constructor de la notificación
    public Notificacion(String titulo, String mensaje, String tipo, String hora, int numeroAvisos, int icono) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.hora = hora;
        this.numeroAvisos = numeroAvisos;
        this.icono = icono;
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public String getHora() {
        return hora;
    }

    public int getNumeroAvisos() {
        return numeroAvisos;
    }

    public int getIcono() {
        return icono;
    }

    // Setters
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setNumeroAvisos(int numeroAvisos) {
        this.numeroAvisos = numeroAvisos;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }
}
