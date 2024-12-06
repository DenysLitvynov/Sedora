package com.example.sedora.presentation.managers;

import android.util.Log;

import com.example.sedora.model.Notificacion;
import com.example.sedora.model.SensorData;
import com.example.sedora.model.Usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    public final FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public static void guardarUsuario(final FirebaseUser user) {
        Usuario usuario = new Usuario(user.getDisplayName(), user.getEmail());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(user.getUid()).set(usuario);
    }

    public void guardarToma(FirebaseUser user, SensorData data) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        db.collection("usuarios")
                .document(user.getUid())
                .collection("Datos")
                .document(fecha)
                .collection("Tomas")
                .add(data)
                .addOnSuccessListener(docRef -> {
                    System.out.println("Toma guardada: " + docRef.getId());
                    actualizarResumenDiario(user, data); // Actualizar promedios después de guardar la toma
                })
                .addOnFailureListener(e -> System.err.println("Error al guardar toma: " + e.getMessage()));
    }

    public void obtenerUltimaTomaDeHoy(FirebaseUser user, SensorDataCallback callback) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        db.collection("usuarios")
                .document(user.getUid())
                .collection("Datos")
                .document(fecha)
                .collection("Tomas")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        SensorData data = document.toObject(SensorData.class);
                        callback.onSensorDataObtained(data);
                        Log.d("FirebaseHelper", "Datos obtenidos: " + data);
                    } else {
                        callback.onSensorDataObtained(null);
                        Log.d("FirebaseHelper", "No se encontraron datos para hoy");
                    }
                });
    }


    public interface SensorDataCallback {
        void onSensorDataObtained(SensorData data);
    }

    public void escucharCambiosEnDatos(FirebaseUser usuario, SensorDataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtén la fecha actual para localizar el documento del día
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String path = "usuarios/" + usuario.getUid() + "/Datos/" + fecha + "/Tomas";

        db.collection(path)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1) // Solo el último documento agregado
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("FirebaseHelper", "Error al escuchar cambios en los datos", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            SensorData data = doc.toObject(SensorData.class);
                            callback.onSensorDataObtained(data); // Llama al callback
                        }
                    } else {
                        Log.d("FirebaseHelper", "No se encontraron datos en la subcolección 'Tomas'");
                        callback.onSensorDataObtained(null);
                    }
                });
    }

    public void guardarNotificacion(FirebaseUser user, Notificacion notificacion) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        db.collection("usuarios")
                .document(user.getUid())
                .collection("notificaciones")
                .add(notificacion)
                .addOnSuccessListener(docRef -> {
                    System.out.println("Notificación guardada: " + docRef.getId()); })
                .addOnFailureListener(e -> System.err.println("Error al guardar notificación: " + e.getMessage())); }

    public void actualizarResumenDiario(FirebaseUser user, SensorData nuevaToma) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        db.collection("usuarios")
                .document(user.getUid())
                .collection("Datos")
                .document(fecha)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double luminosidadPromedio = documentSnapshot.getDouble("luminosidadPromedio");
                        Double ruidoPromedio = documentSnapshot.getDouble("ruidoPromedio");
                        Double presion1Promedio = documentSnapshot.getDouble("presion1Promedio");
                        Double presion2Promedio = documentSnapshot.getDouble("presion2Promedio");
                        Double temperaturaPromedio = documentSnapshot.getDouble("temperaturaPromedio");
                        Double humedadPromedio = documentSnapshot.getDouble("humedadPromedio");
                        Double proximidadPromedio = documentSnapshot.getDouble("proximidadPromedio");
                        int count = documentSnapshot.getLong("count").intValue();

                        if (luminosidadPromedio == null) luminosidadPromedio = 0.0;
                        if (ruidoPromedio == null) ruidoPromedio = 0.0;
                        if (presion1Promedio == null) presion1Promedio = 0.0;
                        if (presion2Promedio == null) presion2Promedio = 0.0;
                        if (temperaturaPromedio == null) temperaturaPromedio = 0.0;
                        if (humedadPromedio == null) humedadPromedio = 0.0;
                        if (proximidadPromedio == null) proximidadPromedio = 0.0;

                        luminosidadPromedio = (luminosidadPromedio * count + nuevaToma.getLuminosidad()) / (count + 1);
                        ruidoPromedio = (ruidoPromedio * count + nuevaToma.getRuido()) / (count + 1);
                        presion1Promedio = (presion1Promedio * count + nuevaToma.getPresion1()) / (count + 1);
                        presion2Promedio = (presion2Promedio * count + nuevaToma.getPresion2()) / (count + 1);
                        temperaturaPromedio = (temperaturaPromedio * count + nuevaToma.getTemperatura()) / (count + 1);
                        humedadPromedio = (humedadPromedio * count + nuevaToma.getHumedad()) / (count + 1);
                        proximidadPromedio = (proximidadPromedio * count + nuevaToma.getProximidad()) / (count + 1);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("luminosidadPromedio", luminosidadPromedio);
                        updates.put("ruidoPromedio", ruidoPromedio);
                        updates.put("presion1Promedio", presion1Promedio);
                        updates.put("presion2Promedio", presion2Promedio);
                        updates.put("temperaturaPromedio", temperaturaPromedio);
                        updates.put("humedadPromedio", humedadPromedio);
                        updates.put("proximidadPromedio", proximidadPromedio);
                        updates.put("count", count + 1);

                        db.collection("usuarios")
                                .document(user.getUid())
                                .collection("Datos")
                                .document(fecha)
                                .update(updates);
                    } else {
                        Map<String, Object> resumen = new HashMap<>();
                        resumen.put("fecha", fecha);
                        resumen.put("luminosidadPromedio", nuevaToma.getLuminosidad());
                        resumen.put("ruidoPromedio", nuevaToma.getRuido());
                        resumen.put("presion1Promedio", nuevaToma.getPresion1());
                        resumen.put("presion2Promedio", nuevaToma.getPresion2());
                        resumen.put("temperaturaPromedio", nuevaToma.getTemperatura());
                        resumen.put("humedadPromedio", nuevaToma.getHumedad());
                        resumen.put("proximidadPromedio", nuevaToma.getProximidad());
                        resumen.put("count", 1);

                        db.collection("usuarios")
                                .document(user.getUid())
                                .collection("Datos")
                                .document(fecha)
                                .set(resumen);
                    }
                });
    }



    public void eliminarTomasDelDia(FirebaseUser user) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        db.collection("usuarios")
                .document(user.getUid())
                .collection("Datos")
                .document(fecha)
                .collection("Tomas")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        doc.getReference().delete();
                    }
                    System.out.println("Subcolección 'Tomas' eliminada para la fecha: " + fecha);
                })
                .addOnFailureListener(e -> System.err.println("Error al eliminar 'Tomas': " + e.getMessage()));
    }


    public void calcularPuntuacionDiaria(FirebaseUser user, PuntuacionCallback callback) {
        // Calcular la fecha de hoy en formato "yyyy-MM-dd"
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Acceder al documento del día de hoy
        db.collection("usuarios")
                .document(user.getUid())
                .collection("Datos")
                .document(fechaHoy) // Usamos la fecha como documento
                .collection("Tomas") // Accedemos a la subcolección "Tomas"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float puntuacion = 0;
                        int count = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            puntuacion += evaluarDatos(document); // Evaluamos los datos de cada "Toma"
                            count++;
                        }

                        if (count > 0) {
                            puntuacion /= count; // Calculamos el promedio de la puntuación
                        }

                        // Llamamos al callback con la puntuación calculada
                        callback.onPuntuacionCalculada(puntuacion);
                    } else {
                        callback.onPuntuacionCalculada(0); // Si hay error, la puntuación es 0
                    }
                });
    }

    private float evaluarDatos(QueryDocumentSnapshot document) {
        float score = 100;

        double luminosidad = document.getDouble("luminosidad");
        double ruido = document.getDouble("ruido");
        double presion1 = document.getDouble("presion1");
        double presion2 = document.getDouble("presion2");
        double temperatura = document.getDouble("temperatura");
        double humedad = document.getDouble("humedad");
        double proximidad = document.getDouble("proximidad");

        if (luminosidad < 200 || luminosidad > 400) score -= 10;
        if (ruido > 60) score -= 15;
        if (presion1 < 1.2 || presion2 < 1.2) score -= 20;
        if (temperatura < 20 || temperatura > 24) score -= 10;
        if (humedad < 30 || humedad > 50) score -= 10;
        if (proximidad < 0.5) score -= 15;

        return Math.max(score, 0);
    }

    public interface PuntuacionCallback {
        void onPuntuacionCalculada(float puntuacion);
    }
}
