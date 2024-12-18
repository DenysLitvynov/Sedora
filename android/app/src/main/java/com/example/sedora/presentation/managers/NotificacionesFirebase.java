package com.example.sedora.presentation.managers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.sedora.R;
import com.example.sedora.model.Notificacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificacionesFirebase {

    private static final String TAG = "NotificacionesFirebase";
    private static final String CANAL_ID = "Canal_sedora";
    private static final String CANAL_NOMBRE = "Mis Notificaciones";
    private static final String CANAL_DESCRIPCION = "Canal de notificaciones de Sedora";

    private final Context context;
    private final NotificationManager notificationManager;
    private NotificationCompat.Builder notificacion;
    private String titulo;
    private String descripcion;
    private String descripcionLarga;
    private int iconoGrande;

    public NotificacionesFirebase(Context context, String titulo, String descripcion, String descripcionLarga, int iconoGrande, Class<?> actividadDestino) {
        this.context = context;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.descripcionLarga = descripcionLarga;
        this.iconoGrande = iconoGrande;

        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        configurarCanalNotificaciones();
        configurarNotificacion(titulo, descripcion, descripcionLarga, iconoGrande, actividadDestino);
    }

    public NotificacionesFirebase(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        configurarCanalNotificaciones();
    }

    private void configurarCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CANAL_ID,
                    CANAL_NOMBRE,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CANAL_DESCRIPCION);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void configurarNotificacion(String titulo, String textoNotificacion, String parrafoNotificacion, int iconoGrande, Class<?> actividadDestino) {
        Intent intent;
        if (actividadDestino != null) {
            intent = new Intent(context, actividadDestino);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            Log.w(TAG, "La actividad destino es nula. La notificación no tendrá un PendingIntent asociado.");
            intent = new Intent(); // Intent vacío
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        notificacion = new NotificationCompat.Builder(context, CANAL_ID)
                .setContentTitle(titulo)
                .setContentText(textoNotificacion)
                .setSmallIcon(R.drawable.sedora_logo)
                .setColor(ContextCompat.getColor(context, R.color.verde_primario))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(parrafoNotificacion))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconoGrande))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    public void lanzarNotificacion() {
        if (notificacion == null) {
            Log.e(TAG, "No se configuró la notificación antes de lanzarla.");
            return;
        }

        int uniqueNotificationId = (titulo != null) ? titulo.hashCode() : (int) System.currentTimeMillis();
        if (notificationManager != null) {
            notificationManager.notify(uniqueNotificationId, notificacion.build());
            Log.d(TAG, "Notificación lanzada con ID: " + uniqueNotificationId);
        } else {
            Log.e(TAG, "NotificationManager es nulo. No se pudo lanzar la notificación.");
        }

        guardarNotificacionEnFirebase();
    }


    private void guardarNotificacionEnFirebase() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            Notificacion notificacionData = new Notificacion(
                    titulo,
                    descripcion,
                    descripcionLarga,
                    new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date()),
                    1,
                    iconoGrande
            );
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.guardarNotificacion(usuario, notificacionData);
        } else {
            Log.w(TAG, "El usuario no está autenticado. No se guardó la notificación en Firebase.");
        }
    }
}
