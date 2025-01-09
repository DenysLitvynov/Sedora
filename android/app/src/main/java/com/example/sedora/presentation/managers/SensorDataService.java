package com.example.sedora.presentation.managers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.sedora.R;
import com.example.sedora.model.SensorData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class SensorDataService extends Service {

    private static final String CHANNEL_ID = "SEDORA_SENSOR_SERVICE";
    private FirebaseHelper firebaseHelper;
    private final NotificationVerifier notificationVerifier=new NotificationVerifier(this);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SensorDataService", "Servicio iniciado");

        // Crear el canal de notificación para el servicio
        createNotificationChannel();

        // Iniciar el servicio en primer plano
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio de Sensores Activo")
                .setContentText("Recolectando datos del sensor")
                .setSmallIcon(R.mipmap.ic_launcher) // Usa el ícono predeterminado de tu app
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);

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
//            // Usamos WorkManager para ejecutar NotificationWorker
//            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                    .setInputData(createInputDataForWorker(data))  // Pasamos los datos de SensorData al worker
//                    .build();
//            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest); // Encolamos el worker
            notificationVerifier.verificarDatosYNotificar(data);

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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal del Servicio de Sensores",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
