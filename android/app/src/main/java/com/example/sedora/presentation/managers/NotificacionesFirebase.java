package com.example.sedora.presentation.managers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.sedora.App;
import com.example.sedora.R;
import com.example.sedora.model.Notificacion;

import java.util.List;

public class NotificacionesFirebase {

    private NotificationManager notificationManager;//Notification manager de android
    static final String CANAL_ID = "Canal_sedora";
    static final int NOTIFICACION_ID = 1;
    private Context context; // Contexto necesario para acceder a recursos del sistema
    private String titulo;
    private String descripcion;
    private  NotificationCompat.Builder notificacion;

    public NotificacionesFirebase(Context context,String titulo,String descripcion){
        this.context=context;
        this.titulo=titulo;
        this.descripcion=descripcion;
        configurar_canal_Noti();
        configurarNoti(titulo,descripcion);
    }

    private void configurar_canal_Noti() {

//        if (notificationManager == null) {
//            notificationManager = (NotificationManager) App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
//    }

        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    private NotificationCompat.Builder configurarNoti(String titulo,String texto_de_la_noti){
      notificacion = new NotificationCompat.Builder(context, CANAL_ID)
                        .setContentTitle(titulo)
                        .setContentText(texto_de_la_noti)
                        .setSmallIcon(R.drawable.sedora_logo);
        return notificacion;
    }

    public void lanzarNotificacion(){
        notificationManager.notify(NOTIFICACION_ID, notificacion.build());
    }


    //    if(horasSentadoActual>Horas sentado recomenada){
    //lanzar noti
//
//    }

}
