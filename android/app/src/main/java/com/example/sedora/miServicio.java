package com.example.sedora;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sedora.presentation.managers.NotificacionesFirebase;
import com.example.sedora.presentation.views.PantallaInicioActivity;
import com.example.sedora.presentation.views.RecyclerActivity;

//Para empezar el servicio, meter esto en el on create de la actividad;

//INICIO DE SERVICIO
//Intent intent = new Intent(this, miServicio.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//          startForegroundService(intent);
//        }

////INICIO DE SERVICIO

//ESTE SERVICIO ES SOLO PARA CORRER COSAS DE FONDO FUERA Y DENTRO DE LA APP.

public class miServicio extends Service {

    private static final String CHANNEL_ID = "miServicioChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    //TODO LO QUE QUIERAN CORRER DE FONDO LLAMARLO AQUI:
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Crear la notificación para el servicio en primer plano
        //Esta solo existe porque te pide una noti para empezar el servicio
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_MIN).build();

        NotificacionesFirebase notiHoras= new NotificacionesFirebase(this,"Horas Sentado","LEVANTATE",null,R.drawable.icono_silla, PantallaInicioActivity.class);
        NotificacionesFirebase notiPostura=new NotificacionesFirebase(this,"POSTURA ","ARREGLA LA POSTURA",null, R.drawable.boton_verde, RecyclerActivity.class);

        notiHoras.lanzarNotificacionFirebase("Prueba__BORRAR_DESPUES",
                "PRUEBITA","Horas",1);
        notiPostura.lanzarNotificacionFirebase("Prueba__BORRAR_DESPUES",
                "PRUEBITA","POSTURA",1);


        // Iniciar el servicio en primer plano
        startForeground(NOTIFICATION_ID, notification);

        // Lógica del servicio
        // Por ejemplo, inicializar un listener o ejecutar tareas periódicas

        return START_STICKY; // Reiniciar servicio automáticamente si el sistema lo detiene
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
