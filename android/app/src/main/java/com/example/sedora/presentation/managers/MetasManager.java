package com.example.sedora.presentation.managers;

import android.util.Log;
import com.example.sedora.model.Meta;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

    public void pasarMetaActualAConseguida(MetaUpdateCallback callback) {
        // Buscar la meta actual
        db.collection("metas")
                .whereEqualTo("estado", "actual")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String metaActualId = task.getResult().getDocuments().get(0).getId();
                        Meta metaActual = task.getResult().getDocuments().get(0).toObject(Meta.class);
                        if (metaActual != null && metaActual.getProgresoActual() >= metaActual.getProgresoTotal()) {

                            // Cambiar la meta actual a "completada"
                            db.collection("metas").document(metaActualId)
                                    .update("estado", "completada")
                                    .addOnSuccessListener(aVoid -> {
                                        // Buscar la siguiente meta pendiente
                                        db.collection("metas")
                                                .whereEqualTo("estado", "pendiente")
                                                .orderBy("numeroMeta", Query.Direction.ASCENDING)
                                                .limit(1)
                                                .get()
                                                .addOnCompleteListener(nextTask -> {
                                                    if (nextTask.isSuccessful() && !nextTask.getResult().isEmpty()) {
                                                        // Promover la siguiente meta a "actual"
                                                        String nextMetaId = nextTask.getResult().getDocuments().get(0).getId();
                                                        db.collection("metas").document(nextMetaId)
                                                                .update("estado", "actual")
                                                                .addOnSuccessListener(aVoidNext -> {
                                                                    Log.d("MetasManager", "Siguiente meta promovida a 'actual'");
                                                                    callback.onMetaUpdated();
                                                                })
                                                                .addOnFailureListener(callback::onError);
                                                    } else if (nextTask.getException() != null && nextTask.getException().getMessage().contains("FAILED_PRECONDITION")) {
                                                        Log.e("MetasManager", "Índice compuesto necesario. Por favor crea el índice en Firestore.");
                                                        callback.onError(new Exception("Por favor, crea el índice necesario en Firestore."));
                                                    } else {
                                                        Log.d("MetasManager", "No hay metas pendientes.");
                                                        callback.onMetaUpdated();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    if (e.getMessage() != null && e.getMessage().contains("FAILED_PRECONDITION")) {
                                                        Log.e("MetasManager", "Índice compuesto necesario. Por favor crea el índice en Firestore.");
                                                        callback.onError(new Exception("Por favor, crea el índice necesario en Firestore."));
                                                    } else {
                                                        callback.onError(e);
                                                    }
                                                });
                                    })
                                    .addOnFailureListener(callback::onError);
                        } else {
                            callback.onMetaUpdated();
                        }
                    } else {
                        callback.onError(new Exception("No se encontró una meta actual."));
                    }
                })
                .addOnFailureListener(callback::onError);
    }
}