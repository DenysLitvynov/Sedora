package com.example.sedora.firebase;

import com.example.sedora.model.Meta;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MetasSeeder {

    public static void seedMetas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Meta> metas = new ArrayList<>();

        metas.add(new Meta("Mantente correcto 1 h", "Mantén una posición correcta durante 1 hora.","icono_sentado", "01"));
        metas.add(new Meta("¡No trabajes tanto!", "Levántate de la silla al menos 5 veces en un día.", "levantarse", "02"));
        metas.add(new Meta("¡Mantente correcto 2 h!", "Mantén tu posición correcta durante 2 horas.", "icono_sentado", "03"));
        metas.add(new Meta("Luz suficiente", "No recibas alertas por falta de luz durante 1 hora.", "icono_iluminacion", "04"));
        metas.add(new Meta("Luz suficiente II", "No recibas alertas por falta de luz durante 2 horas.", "icono_iluminacion", "05"));
        metas.add(new Meta("Perfecto por 3 horas", "Mantén una postura perfecta sin recibir alertas durante 3 horas seguidas.", "icono_sentado2", "06"));
        metas.add(new Meta("No todo es trabajo", "Levántate de la silla al menos 3 veces en 4 horas.","levantarse", "07"));
        metas.add(new Meta("Mantente correcto 4 h", "Mantén una postura correcta durante 4 horas.", "icono_sentado2", "08"));
        metas.add(new Meta("Luz suficiente III", "No recibas alertas por falta de luz durante 3 horas.",  "icono_iluminacion", "09"));
        metas.add(new Meta("No tan cerca", "Evita alertas de proximidad a la pantalla durante 2 horas.", "proximidad_pantalla", "10"));
        metas.add(new Meta("Corrige rápido", "Responde a una alerta de postura poniéndote correctamente 20 veces.", "icono_sentado2", "11"));
        metas.add(new Meta("Estírate y sigue", "Haz al menos 2 pausas durante 2 horas de trabajo.","estiramiento_meta", "12"));
        metas.add(new Meta("Postura perfecta", "Completa un día laboral con menos de 5 alertas de postura incorrecta.", "postura_incorrecta", "13"));
        metas.add(new Meta("Distancia saludable", "Mantén una distancia adecuada del dispositivo durante 4 horas sin alertas de proximidad.", "proximidad_pamntalla", "14"));
        metas.add(new Meta("Aumenta tus pausas", "Levántate de la silla al menos 8 veces en un día.", "levantarse", "15"));
        metas.add(new Meta("Relaja tu espalda", "Mantén la espalda recta durante 2 horas sin recibir alertas de corrección.", "icono_sentado2", "16"));
        metas.add(new Meta("Sin distracciones", "Evita cualquier alerta de postura o luz durante 3 horas.", "icono_campana_2", "17"));
        metas.add(new Meta("Postura de oro", "Mantén una postura correcta durante 1 jornada laboral completa.", "icono_sentado2", "18"));
        metas.add(new Meta("Luz y postura equilibrada", "Trabaja en un ambiente iluminado y con postura correcta durante 4 horas consecutivas.", "ambiente_bueno", "19"));
        metas.add(new Meta("Dios vertebral", "Mantén la espalda recta y evita alertas de postura durante 5 horas en un día.", "icono_sentado2", "20"));

        // Añadir las metas a Firestore
        for (Meta meta : metas) {
            db.collection("metas").document(String.valueOf(meta.getNumeroMeta())).set(meta)
                    .addOnSuccessListener(aVoid -> System.out.println("Meta añadida: " + meta.getNombre()))
                    .addOnFailureListener(e -> System.err.println("Error al añadir meta: " + e.getMessage()));
        }
    }
}