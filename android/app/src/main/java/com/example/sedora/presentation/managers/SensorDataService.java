package com.example.sedora.presentation.managers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sedora.R;
import com.example.sedora.model.Notificacion;

import java.util.List;

public class SensorDataService extends Service {

    private static final String CHANNEL_ID = "SEDORA_SENSOR_SERVICE";
    private static final int NOTIFICATION_ID = 1;
    private NotificacionManager notificacionManager;
    private Handler handler;
    private Runnable notificationRunnable;
    private boolean areNotificationsBlocked = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SensorDataService", "Servicio iniciado");

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio de Sensores Activo")
                .setContentText("Recolectando datos del sensor")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        notificacionManager = new NotificacionManager();
        handler = new Handler();

        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                if (!areNotificationsBlocked) {
                    enviarNotificacionesPredefinidas();
                }
                handler.postDelayed(this, 20000); // 20 segundos
            }
        };

        handler.post(notificationRunnable);
    }

    private void enviarNotificacionesPredefinidas() {
        if (areNotificationsBlocked) {
            Log.d("SensorDataService", "Notificaciones bloqueadas, no se enviarán.");
            return;
        }

        List<Notificacion> notificacionesVisibles = notificacionManager.getNotificaciones();

        for (Notificacion notificacion : notificacionesVisibles) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(notificacion.getTitulo())
                    .setContentText(notificacion.getMensaje())
                    .setSmallIcon(notificacion.getIcono())
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(notificacion.getTitulo().hashCode(), notification);
            }
        }
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("SensorDataService", "Servicio detenido");
        handler.removeCallbacks(notificationRunnable);
        super.onDestroy();
    }
}
