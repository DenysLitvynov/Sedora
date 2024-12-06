package com.example.sedora.presentation.managers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.sedora.model.SensorData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class SensorDataService extends Service {

    private FirebaseHelper firebaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SensorDataService", "Servicio iniciado");
        firebaseHelper = new FirebaseHelper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            firebaseHelper.escucharCambiosEnDatos(usuario, this::verificarDatosYNotificar);
        } else {
            Log.d("SensorDataService", "Usuario no autenticado");
        }
        return START_STICKY; // Mantener el servicio activo
    }

    private void verificarDatosYNotificar(SensorData data) {
        if (data != null) {
            // Usamos WorkManager para ejecutar NotificationWorker
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInputData(createInputDataForWorker(data))  // Pasamos los datos de SensorData al worker
                    .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest); // Encolamos el worker
        }
    }

    @Override
    public void onDestroy() {
        Log.d("SensorDataService", "Servicio detenido");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Data createInputDataForWorker(SensorData data) {
        // Convertimos SensorData a JSON y lo pasamos como entrada al Worker
        String jsonData = new Gson().toJson(data);
        return new Data.Builder()
                .putString("sensor_data", jsonData) // Pasamos los datos como un String en JSON
                .build();
    }
}
