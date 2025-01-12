package com.example.sedora.presentation.managers;

import com.example.sedora.R;
import com.example.sedora.model.Notificacion;

import java.util.ArrayList;
import java.util.List;

public class NotificacionManager {

    private List<Notificacion> notificaciones;
    private boolean luzEnabled = true;
    private boolean sonidoEnabled = true;
    private boolean temperaturaEnabled = true;
    private boolean posturaEnabled = true;
    private boolean distanciaEnabled = true;
    private boolean estiramientosEnabled = true;
    private boolean descansosEnabled = true;
    private boolean hidratacionEnabled = true;

    public NotificacionManager() {
        notificaciones = new ArrayList<>();
        notificaciones.add(new Notificacion("Postura", "Tu postura no es la adecuada, corrígela para evitar molestias.", "Aviso", "13:27 19/09/2024", 4, R.drawable.icono_silla));
        notificaciones.add(new Notificacion("Distancia al monitor", "Estás muy cerca de la pantalla, aléjate para cuidar tu vista.", "Aviso", "14:15 19/09/2024", 2, R.drawable.icono_regla));
        notificaciones.add(new Notificacion("Iluminación", "La iluminación es insuficiente, aumenta la luz en el ambiente para mejorar tu confort visual.", "Aviso", "14:15 19/09/2024", 2, R.drawable.icono_iluminacion));
        notificaciones.add(new Notificacion("Estiramientos", "Has estado sentado mucho tiempo, es hora de hacer unos estiramientos.", "Recordatorio", "14:15 19/09/2024", 2, R.drawable.icono_estiramientos));
        notificaciones.add(new Notificacion("Descanso", "Llevas sentado varias horas. Es hora de hacer una pausa y recargar energías.", "Recomendación", "14:15 19/09/2024", 2, R.drawable.icono_descanso));
        notificaciones.add(new Notificacion("Ruido", "El nivel de ruido es elevado. Intenta reducirlo para mejorar tu concentración.", "Recomendación", "14:15 19/09/2024", 2, R.drawable.icono_ruido));
        notificaciones.add(new Notificacion("Temperatura", "Llevas varias horas sentado. Te sugerimos pausar para recargar energías.", "Recomendación", "14:15 19/09/2024", 2, R.drawable.icono_temperatura));
        notificaciones.add(new Notificacion("Hidratación", "Es recomendable que tomes un momento para beber agua y asegurarte de que estás bien hidratado.", "Recordatorio", "14:15 19/09/2024", 2, R.drawable.icono_hidratacion));
    }

    public List<Notificacion> getNotificaciones() {
        List<Notificacion> habilitadas = new ArrayList<>();
        for (Notificacion n : notificaciones) {
            if ((n.getTitulo().equals("Postura") && posturaEnabled) ||
                    (n.getTitulo().equals("Distancia al monitor") && distanciaEnabled) ||
                    (n.getTitulo().equals("Iluminación") && luzEnabled) ||
                    (n.getTitulo().equals("Estiramientos") && estiramientosEnabled) ||
                    (n.getTitulo().equals("Descanso") && descansosEnabled) ||
                    (n.getTitulo().equals("Ruido") && sonidoEnabled) ||
                    (n.getTitulo().equals("Temperatura") && temperaturaEnabled) ||
                    (n.getTitulo().equals("Hidratación") && hidratacionEnabled)) {
                habilitadas.add(n);
            }
        }
        return habilitadas;
    }

    // Métodos para habilitar o deshabilitar todas las notificaciones
    public void permitirNotificaciones() {
        luzEnabled = true;
        sonidoEnabled = true;
        temperaturaEnabled = true;
        posturaEnabled = true;
        distanciaEnabled = true;
        estiramientosEnabled = true;
        descansosEnabled = true;
        hidratacionEnabled = true;
    }

    public void bloquearNotificaciones() {
        luzEnabled = false;
        sonidoEnabled = false;
        temperaturaEnabled = false;
        posturaEnabled = false;
        distanciaEnabled = false;
        estiramientosEnabled = false;
        descansosEnabled = false;
        hidratacionEnabled = false;
    }

    // Métodos individuales para modificar el estado de cada tipo de notificación
    public void toggleLuz(boolean enabled) {
        luzEnabled = enabled;
    }

    public void toggleSonido(boolean enabled) {
        sonidoEnabled = enabled;
    }

    public void toggleTemperatura(boolean enabled) {
        temperaturaEnabled = enabled;
    }

    public void togglePostura(boolean enabled) {
        posturaEnabled = enabled;
    }

    public void toggleDistancia(boolean enabled) {
        distanciaEnabled = enabled;
    }

    public void toggleEstiramientos(boolean enabled) {
        estiramientosEnabled = enabled;
    }

    public void toggleDescansos(boolean enabled) {
        descansosEnabled = enabled;
    }

    public void toggleHidratacion(boolean enabled) {
        hidratacionEnabled = enabled;
    }
}
