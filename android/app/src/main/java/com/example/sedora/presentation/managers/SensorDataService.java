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
import com.example.sedora.model.SensorData;

import java.util.List;

public class SensorDataService extends Service {

    private static final String CHANNEL_ID = "SEDORA_SENSOR_SERVICE";
    private FirebaseHelper firebaseHelper;
    private final NotificationVerifier notificationVerifier = new NotificationVerifier(this);
    private static final int NOTIFICATION_ID = 1;
    private NotificacionManager notificacionManager;
    public SensorDataService() {
        // Asegúrate de inicializar NotificacionManager
        notificacionManager = new NotificacionManager();
    }
    private Handler handler;
    private Runnable notificationRunnable;
    public static boolean areNotificationsBlocked = false;

    public static boolean isNotiLuzBlocked = false;
    public static boolean isNotiSonidoBlocked = false;
    public static boolean isNotiTemperaturaBlocked = false;
    public static boolean isNotiPosturaBlocked = false;
    public static boolean isNotiDistanciaBlocked = false;
    public static boolean isNotiEstiramientosBlocked = false;
    public static boolean isNotiDescansosBlocked = false;
    public static boolean isNotiHidratacionBlocked = false;


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

        firebaseHelper = new FirebaseHelper();
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

    private void verificarDatosYNotificar(SensorData data) {
        if (data != null) {
            notificationVerifier.verificarDatosYNotificar(data);
        }
    }

    private void enviarNotificacionesPredefinidas() {
        if (areNotificationsBlocked || !notificacionManager.areNotificationsEnabled()) {
            Log.d("SensorDataService", "Notificaciones bloqueadas globalmente, no se enviarán.");
            return;
        }

        List<Notificacion> notificacionesVisibles = notificacionManager.getNotificaciones();
        for (Notificacion notificacion : notificacionesVisibles) {
            enviarNotificacionIndividual(notificacion);
        }
    }


    public void enviarNotificacionIndividual(Notificacion notificacion) {
        Log.d("SensorDataService", "Intentando enviar notificación: " + notificacion.getTitulo());

        if ((notificacion.getTitulo().equals("Postura") && !isNotiPosturaBlocked) ||
                (notificacion.getTitulo().equals("Distancia al monitor") && !isNotiDistanciaBlocked) ||
                (notificacion.getTitulo().equals("Iluminación") && !isNotiLuzBlocked) ||
                (notificacion.getTitulo().equals("Estiramientos") && !isNotiEstiramientosBlocked) ||
                (notificacion.getTitulo().equals("Descanso") && !isNotiDescansosBlocked) ||
                (notificacion.getTitulo().equals("Ruido") && !isNotiSonidoBlocked) ||
                (notificacion.getTitulo().equals("Temperatura") && !isNotiTemperaturaBlocked) ||
                (notificacion.getTitulo().equals("Hidratación") && !isNotiHidratacionBlocked)) {

            Log.d("SensorDataService", "Notificación permitida: " + notificacion.getTitulo());
            // Enviar notificación
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
        } else {
            Log.d("SensorDataService", "Notificación bloqueada: " + notificacion.getTitulo());
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
