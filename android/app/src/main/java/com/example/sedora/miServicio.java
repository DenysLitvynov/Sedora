package com.example.sedora;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class miServicio extends Service {

    private static final String CHANNEL_ID = "miServicioChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Crear la notificación para el servicio en primer plano
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio en ejecución")
                .setContentText("El servicio está funcionando en segundo plano.")
                .setSmallIcon(R.drawable.sedora_logo) // Usar un ícono válido
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Asegurarse de que sea visible
                .build();

        // Iniciar el servicio en primer plano
        startForeground(NOTIFICATION_ID, notification);

        // Lógica del servicio
        // Por ejemplo, inicializar un listener o ejecutar tareas periódicas

        return START_STICKY; // Reiniciar servicio automáticamente si el sistema lo detiene
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Detener tareas o listeners cuando el servicio sea destruido
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Este servicio no se conecta a un cliente
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal de Servicio",
                    NotificationManager.IMPORTANCE_DEFAULT // Cambiar a un nivel de importancia visible
            );
            channel.setDescription("Canal para el servicio en primer plano");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
