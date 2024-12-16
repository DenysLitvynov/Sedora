package com.example.sedora;

import android.app.Application;
import android.util.Log;
import android.content.Intent;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.sedora.presentation.managers.NotificationWorker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.TimeUnit;

public class SedoraApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PeriodicWorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "NotificationWork",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationWorkRequest
        );

        Log.d("SedoraApp", "Aplicación iniciada");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("SedoraApp", "Usuario autenticado: " + currentUser.getEmail());
            iniciarSensorDataService();
        } else {
            Log.d("SedoraApp", "Ningún usuario autenticado");
        }
    }
    private void iniciarSensorDataService() {
        Intent intent = new Intent(this, com.example.sedora.presentation.managers.SensorDataService.class);
        startService(intent);
        Log.d("SedoraApp", "Servicio SensorDataService iniciado desde SedoraApp");
    }
}

