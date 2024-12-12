package com.example.sedora.firebase;

import com.example.sedora.model.Meta;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MetasSeeder {

    public static void seedMetas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Meta> metas = new ArrayList<>();

        metas.add(new Meta("Mantente correcto 1 h", "Mantén una posición correcta durante 1 hora.", 1, 0, "icono_sentado", "01", "actual"));
        metas.add(new Meta("¡No trabajes tanto!", "Levántate de la silla al menos 5 veces en un día.", 5, 0, "levantarse", "02", "pendiente"));
        metas.add(new Meta("¡Mantente correcto 2 h!", "Mantén tu posición correcta durante 2 horas.", 2, 0, "icono_sentado", "03", "pendiente"));
        metas.add(new Meta("Trabaja con luz suficiente", "No recibas alertas por falta de luz durante 1 hora.", 1, 0, "icono_iluminacion", "04", "pendiente"));
        metas.add(new Meta("Trabaja con luz suficiente II", "No recibas alertas por falta de luz durante 2 horas.", 2, 0, "icono_iluminacion", "05", "pendiente"));
        metas.add(new Meta("Perfecto por 3 horas", "Mantén una postura perfecta sin recibir alertas durante 3 horas seguidas.", 3, 0, "icono_sentado2", "06", "pendiente"));
        metas.add(new Meta("No todo es trabajo", "Levántate de la silla al menos 3 veces en 4 horas.", 3, 0, "levantarse", "07", "pendiente"));
        metas.add(new Meta("Mantente correcto 4 h", "Mantén una postura correcta durante 4 horas.", 4, 0, "icono_sentado2", "08", "pendiente"));
        metas.add(new Meta("Aprovecha la luz natural", "No recibas alertas por falta de luz durante 3 horas.", 3, 0, "icono_iluminacion", "09", "pendiente"));
        metas.add(new Meta("No más alertas por proximidad", "Evita alertas de proximidad a la pantalla durante 2 horas.", 2, 0, "proximidad_pantalla", "10", "pendiente"));
        metas.add(new Meta("Corrige rápido", "Responde a una alerta de postura poniéndote correctamente 20 veces.", 20, 0, "icono_sentado2", "11", "pendiente"));
        metas.add(new Meta("Estírate y sigue", "Haz al menos 2 pausas durante 2 horas de trabajo.", 2, 0, "estiramiento_meta", "12", "pendiente"));
        metas.add(new Meta("Postura perfecta 1 día", "Completa un día laboral con menos de 5 alertas de postura incorrecta.", 5, 0, "postura_incorrecta", "13", "pendiente"));
        metas.add(new Meta("Distancia saludable", "Mantén una distancia adecuada del dispositivo durante 4 horas sin alertas de proximidad.", 4, 0, "proximidad_pamntalla", "14", "pendiente"));
        metas.add(new Meta("Aumenta tus pausas", "Levántate de la silla al menos 8 veces en un día.", 8, 0, "levantarse", "15", "pendiente"));
        metas.add(new Meta("Relaja tu espalda", "Mantén la espalda recta durante 2 horas sin recibir alertas de corrección.", 2, 0, "icono_sentado2", "16", "pendiente"));
        metas.add(new Meta("Trabaja sin distracciones", "Evita cualquier alerta de postura o luz durante 3 horas.", 3, 0, "icono_campana_2", "17", "pendiente"));
        metas.add(new Meta("Postura de oro", "Mantén una postura correcta durante 1 jornada laboral completa.", 8, 0, "icono_sentado2", "18", "pendiente"));
        metas.add(new Meta("Luz y postura equilibrada", "Trabaja en un ambiente iluminado y con postura correcta durante 4 horas consecutivas.", 4, 0, "ambiente_bueno", "19", "pendiente"));
        metas.add(new Meta("Dios del equilibrio vertebral", "Mantén la espalda recta y evita alertas de postura durante 5 horas en un día.", 5, 0, "icono_sentado2", "20", "pendiente"));

        // Añadir las metas a Firestore
        for (Meta meta : metas) {
            db.collection("metas").document(String.valueOf(meta.getNumeroMeta())).set(meta)
                    .addOnSuccessListener(aVoid -> System.out.println("Meta añadida: " + meta.getNombre()))
                    .addOnFailureListener(e -> System.err.println("Error al añadir meta: " + e.getMessage()));
        }
    }
}