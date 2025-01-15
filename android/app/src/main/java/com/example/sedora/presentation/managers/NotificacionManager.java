package com.example.sedora.presentation.managers;

import android.util.Log;
import android.util.Pair;

import com.example.sedora.R;
import com.example.sedora.model.Notificacion;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public Pair<List<Notificacion>, List<Notificacion>> getNotificaciones() {
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
        return new Pair<>(habilitadas, notificaciones);  // Devuelves ambas listas como un par
    }

    public void addNotificacion(Notificacion notificacion) {
        notificaciones.add(notificacion);
    }

    public void subirNotificacionesAFirestore(FirebaseUser usuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificacionesRef = db.collection("usuarios").document(usuario.getUid()).collection("notificaciones");
        for (Notificacion notificacion : notificaciones) {
            notificacionesRef.add(notificacion);
        }
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

    public void permitirLuz(){
        luzEnabled = true;
    }

    public void permitirSonido(){
        sonidoEnabled = true;
    }

    public void permitirTemperatura(){
        temperaturaEnabled = true;
    }

    public void permitirPostura(){
        posturaEnabled = true;
    }
    public void permitirDistancia(){
        distanciaEnabled = true;
    }

    public void permitirEstiramientos(){
        estiramientosEnabled = true;
    }
    public void permitirDescansos(){
        descansosEnabled = true;
    }

    public void permitirHidratacion(){
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

    public void bloquearLuz(){
        luzEnabled = false;
    }

    public void bloquearSonido(){
        sonidoEnabled = false;
    }

    public void bloquearTemperatura(){
        temperaturaEnabled = false;
    }

    public void bloquearPostura(){
        posturaEnabled = false;
    }
    public void bloquearDistancia(){
        distanciaEnabled = false;
    }

    public void bloquearEstiramientos(){
        estiramientosEnabled = false;
    }
    public void bloquearDescansos(){
        descansosEnabled = false;
    }

    public void bloquearHidratacion(){
        hidratacionEnabled = false;
    }


    public void logEstadosNotificaciones() {
        Log.d("NotificacionManager", "Estado actual de las notificaciones:");
        Log.d("NotificacionManager", "Luz Bloqueada: " + luzEnabled);
        Log.d("NotificacionManager", "Sonido Bloqueada: " + sonidoEnabled);
        Log.d("NotificacionManager", "Temperatura Bloqueada: " + temperaturaEnabled);
        Log.d("NotificacionManager", "Postura Bloqueada: " + posturaEnabled);
        Log.d("NotificacionManager", "Distancia Bloqueada: " + distanciaEnabled);
        Log.d("NotificacionManager", "Estiramientos Bloqueada: " + estiramientosEnabled);
        Log.d("NotificacionManager", "Descansos Bloqueada: " + descansosEnabled);
        Log.d("NotificacionManager", "Hidratación Bloqueada: " + hidratacionEnabled);
    }

    public boolean areNotificationsEnabled() {
        // Devuelve `true` si al menos una notificación está habilitada
        return luzEnabled || sonidoEnabled || temperaturaEnabled || posturaEnabled ||
                distanciaEnabled || estiramientosEnabled || descansosEnabled || hidratacionEnabled;
    }
}

