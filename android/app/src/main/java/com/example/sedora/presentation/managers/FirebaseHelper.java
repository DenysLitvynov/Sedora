package com.example.sedora.presentation.managers;

import com.example.sedora.data.SensorDataList;
import com.example.sedora.model.SensorData;
import com.example.sedora.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    private final FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    //---------------------------------------------------------------
    // Metodo para guardar el usuario en la colección de usuarios
    //---------------------------------------------------------------

    public static void guardarUsuario(final FirebaseUser user) {
        Usuario usuario = new Usuario(user.getDisplayName(),user.getEmail());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(user.getUid()).set(usuario);
    }

    //---------------------------------------------------------------
    // Método para guardar datos de sensores en la subcolección "Data" de cada usuario
    //---------------------------------------------------------------

    public void guardarDatosSensores(FirebaseUser user) {
        SensorDataList sensorDataList = new SensorDataList();
        for (SensorData data : sensorDataList.getListaDatos()) {
            db.collection("usuarios")
                    .document(user.getUid())
                    .collection("Data")
                    .add(data);
        }
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------
}
