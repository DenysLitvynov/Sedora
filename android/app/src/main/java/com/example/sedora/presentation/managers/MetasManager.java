package com.example.sedora.presentation.managers;


import com.example.sedora.firebase.MetasSeeder;
import com.example.sedora.model.Meta;
import com.example.sedora.model.MetaUsuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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


    public void iniciarMetas(){
        MetasSeeder.seedMetas();
    }

    public void iniciarMetasUsuario(FirebaseUser user) {
        if (user == null) {
            System.err.println("Usuario no válido.");
            return;
        }

        List<Meta> listaMetas = new ArrayList<>();

        db.collection("metas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Meta meta = document.toObject(Meta.class);
                            listaMetas.add(meta);
                        }

                        for (Meta meta : listaMetas) {
                            db.collection("usuarios")
                                    .document(user.getUid())
                                    .collection("metasUsuario")
                                    .document(String.valueOf(meta.getNumeroMeta())).set(new MetaUsuario(meta))
                                    .addOnSuccessListener(docRef -> {
                                        System.out.println("Meta añadida con ID: " + docRef.toString());
                                    })
                                    .addOnFailureListener(e -> {
                                        System.err.println("Error al guardar meta: " + e.getMessage());
                                    });
                        }
                    } else {
                        System.err.println("No se encontraron metas en la colección 'metas'.");
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error al recuperar metas: " + e.getMessage());
                });
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


    public void comprobarMetasUsuario(FirebaseUser user) {

        db.collection("usuarios")
                .document(user.getUid())
                .collection("metasUsuario")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() == null || task.getResult().isEmpty()) {
                            System.out.println("El usuario no tiene metas. Es necessario crearlas");
                            iniciarMetasUsuario(user);
                        }
                    } else {
                        System.err.println("Error al comprobar metas: " + task.getException());
                    }
                });
    }

    public void obtenerMetasUsuaruio(FirebaseUser user) {

        db.collection("usuarios")
                .document(user.getUid())
                .collection("metasUsuario")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() == null || task.getResult().isEmpty()) {
                            System.out.println("El usuario no tiene metas. Es necessario crearlas");
                            iniciarMetasUsuario(user);
                        }
                    } else {
                        System.err.println("Error al comprobar metas: " + task.getException());
                    }
                });
    }



    public void pasarMetaActualAConseguida(MetaUpdateCallback callback) {
        System.out.println("asdf");

        // Buscar la meta actual
      /*  db.collection("metasUsuario")
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
        */
    }

}