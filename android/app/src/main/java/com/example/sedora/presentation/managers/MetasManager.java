package com.example.sedora.presentation.managers;

import com.example.sedora.firebase.MetasSeeder;
import com.google.firebase.firestore.FirebaseFirestore;

public class MetasManager {

    private final FirebaseFirestore db;

    public MetasManager() {
        db = FirebaseFirestore.getInstance();
    }

    // Interfaz para manejar actualizaciones de metas
    public interface MetaUpdateCallback {
        void onMetaUpdated();
        void onError(Exception e);
    }
    public void comprobarMetas() {

        db.collection("metas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() == null || task.getResult().isEmpty()) {
                            iniciarMetas();
                            System.out.println("Metas creadas");
                        }
                    }
                });
    }

    public void iniciarMetas(){
        MetasSeeder.seedMetas();
    }

}