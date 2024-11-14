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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


////-----------------------------------
////EJEMPLO DE USO
////-----------------------------------
//Si no tiene o necesita algun elemento, asignarle null o 0 si es un int.

//NotificacionesFirebase notiHoras= new NotificacionesFirebase(this,"Horas Sentado","LEVANTATE",null,R.drawable.icono_silla, PantallaInicioActivity.class);
//NotificacionesFirebase notiPostura=new NotificacionesFirebase(this,"POSTURA ","ARREGLA LA POSTURA",null,R.drawable.icono_sentado2, RecyclerActivity.class);
//
//      notiHoras.lanzarNotificacionFirebase("Prueba__BORRAR_DESPUES",
//                                                     "PRUEBITA","Horas",1);
//
//    notiPostura.lanzarNotificacionFirebase("Prueba__BORRAR_DESPUES",
//                                                       "PRUEBITA","POSTURA",1);
//
////-----------------------------------
////EJEMPLO DE USO
////-----------------------------------

public class NotificacionesFirebase {

    private NotificationManager notificationManager;//Notification manager de android
    static final String CANAL_ID = "Canal_sedora";
    private Context context; // Contexto necesario para acceder a recursos del sistema
    private String titulo;
    private String descripcion;
    private String descripcion_larga;
    private int icono_grande;
    private NotificationCompat.Builder notificacion;
    private Class<?> actividad_de_destino;


    public NotificacionesFirebase(Context context, String titulo, String descripcion_corta,String descripcion_larga,int icono_grande ,Class<?> actividad_de_destino) {
        this.context = context;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.actividad_de_destino=actividad_de_destino;
        this.descripcion_larga=descripcion_larga;
        this.icono_grande=icono_grande;

        configurar_canal_Noti();
        configurarNoti(titulo, descripcion,descripcion_corta,icono_grande,actividad_de_destino);
    }

    public NotificacionesFirebase(Context context){
        this.context=context;
        configurar_canal_Noti();
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


    //-------------------------------------------------------------------
    //Configura la notificacion
    //-------------------------------------------------------------------
    private NotificationCompat.Builder configurarNoti(String titulo, String texto_de_la_noti,String parrafo_noti,int icono_grande, Class<?> actividad_de_destino) {

        // Crear un Intent que apunte a la actividad que se quiere abrir
        Intent intent = new Intent(context, actividad_de_destino);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Configuración para limpiar la pila de actividades

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Agregar el flag
        );


        notificacion = new NotificationCompat.Builder(context, CANAL_ID)
                .setContentTitle(titulo)
                .setContentText(texto_de_la_noti)
                .setSmallIcon(R.drawable.sedora_logo)
                .setColor(ContextCompat.getColor(context, R.color.verde_primario))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(parrafo_noti))
                .setColorized(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icono_grande))
                .setContentIntent(pendingIntent) // Establecer el PendingIntent en la notificación
                .setAutoCancel(true); // Auto-cancelar la notificación al tocarla
        return notificacion;
    }


    //-----------------------------------------------------------------

    //Para lanzar una notificacion normal
    //Esto lo llamas en la clase o actividad que quieras si no necesita nada del firebase
    //-----------------------------------------------------------------
    public void lanzarNotificacion() {
        int uniqueNotificationId = titulo.hashCode(); // Genera un ID único basado en el título
        notificationManager.notify(uniqueNotificationId, notificacion.build());
    }


    //-----------------------------------------------------------------

    //Para lanzar una notificacion basada en algun campo del firebase.

    //Al llamarlo pasale la colecion,documento,el campo que quieres acceder, y el valor que quieres usar para comparar
    //Ejemplo: si las horas sentadaas recomendadas son 2. Pues le pasas 2
    //-----------------------------------------------------------------
    public void lanzarNotificacionFirebase(String pathColecion,String pathDocumento,String campo_a_valorar,int valor_a_comparar) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(pathColecion).document(pathDocumento).addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Firestore", "Error al leer", e);
                        } else if (snapshot == null || !snapshot.exists()) {
                            Log.e("Firestore", "Error: documento no encontrado");
                        } else if (comprobar_valor_Sensor(snapshot, campo_a_valorar, valor_a_comparar)){
                            lanzarNotificacion();
                        }
                    }
                });
    }

    //Comprueba si el valor del documento es mayor o igual del que deberia, si es mayor, devuelve true)
    private boolean comprobar_valor_Sensor(DocumentSnapshot documento, String campo, int valor_a_comprobar) {
        if (documento != null && documento.contains(campo)) {
            // Obtén el valor del campo y asegúrate de que sea un número antes de compararlo
            Long valor = documento.getLong(campo); // O usa Double o Integer dependiendo del tipo esperado
            if (valor != null && valor >= valor_a_comprobar) {
                return true;
            }
        }
        return false;
    }


    //_________________________________________________________________________________________
    //GETTERS Y SETTERS

    public NotificationCompat.Builder getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(NotificationCompat.Builder notificacion) {
        this.notificacion = notificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getIcono_grande() {
        return icono_grande;
    }

    public void setIcono_grande(int icono_grande) {
        this.icono_grande = icono_grande;
    }

    public String getDescripcion_larga() {
        return descripcion_larga;
    }

    public void setDescripcion_larga(String descripcion_larga) {
        this.descripcion_larga = descripcion_larga;
    }
}