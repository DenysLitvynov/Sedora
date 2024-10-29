package com.example.sedora.presentation.managers;

import com.example.sedora.R;
import com.example.sedora.model.Notificacion;

import java.util.ArrayList;
import java.util.List;

public class NotificacionManager {

    private List<Notificacion> notificaciones;


    public NotificacionManager() {
        // Inicializamos la lista de notificaciones
        notificaciones = new ArrayList<>();

        // Añadimos ejemplos de notificaciones predefinidas para que el RecyclerView las pueda mostrar
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
        return notificaciones;
    }

    // Método para añadir una nueva notificación
    public void addNotificacion(Notificacion notificacion) {
        notificaciones.add(notificacion);
    }


}

