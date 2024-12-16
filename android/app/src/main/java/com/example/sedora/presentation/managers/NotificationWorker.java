package com.example.sedora.presentation.managers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sedora.R;
import com.example.sedora.model.SensorData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class NotificationWorker extends Worker {

    private static final double THRESHOLD_POSTURA = 2.0;
    private static final double THRESHOLD_DISTANCIA_MONITOR = 50.0;
    private static final double THRESHOLD_ILUMINACION = 300.0;
    private static final double THRESHOLD_TIEMPO_SENTADO = 120.0; // 2 horas
    private static final double THRESHOLD_DESCANSO = 180.0; // 3 horas
    private static final double THRESHOLD_RUIDO = 10.0;
    private static final double THRESHOLD_TEMPERATURA_MIN = 18.0;
    private static final double THRESHOLD_TEMPERATURA_MAX = 24.0;
    private static final double THRESHOLD_HUMEDAD = 60; // 1.5 litros
    private static final double THRESHOLD_HIDRATACION = 1.5; // 1.5 litros

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Recuperamos los datos pasados al worker desde el inputData
        SensorData data = getSensorDataFromInput();

        // Realizamos la lógica de verificación de los datos y envío de notificaciones
        verificarDatosYNotificar(data);

        return Result.success();  // El worker ha terminado su tarea correctamente
    }

    private SensorData getSensorDataFromInput() {
        // Recuperamos el JSON de los datos de SensorData pasados al Worker
        String json = getInputData().getString("sensor_data");
        return new Gson().fromJson(json, SensorData.class);  // Convertimos el JSON a un objeto SensorData
    }

    public void verificarDatosYNotificar(SensorData data) {
        if (data != null) {
            Context context = getApplicationContext();
            Log.d("NotificationWorker", "Verificando datos: " + data);

            if (data.getPostura() < THRESHOLD_POSTURA) {
                lanzarNotificacion(context, "Postura", "Tu postura no es la adecuada, corrígela para evitar molestias.", R.drawable.icono_silla);
            }

            if (data.getDistanciaMonitor() < THRESHOLD_DISTANCIA_MONITOR) {
                lanzarNotificacion(context, "Distancia al monitor", "Estás muy cerca de la pantalla, aléjate para cuidar tu vista.", R.drawable.icono_regla);
            }

            if (data.getIluminacion() < THRESHOLD_ILUMINACION) {
                lanzarNotificacion(context, "Iluminación", "La iluminación es insuficiente, aumenta la luz en el ambiente.", R.drawable.icono_iluminacion);
            }

            if (data.getTiempoSentado() > THRESHOLD_TIEMPO_SENTADO) {
                lanzarNotificacion(context, "Estiramientos", "Has estado sentado mucho tiempo, es hora de hacer estiramientos.", R.drawable.icono_estiramientos);
            }

            if (data.getTiempoSentado() > THRESHOLD_DESCANSO) {
                lanzarNotificacion(context, "Descanso", "Es hora de hacer una pausa y recargar energías.", R.drawable.icono_descanso);
            }

            if (data.getRuido() > THRESHOLD_RUIDO) {
                lanzarNotificacion(context, "Ruido", "El nivel de ruido es elevado. Intenta reducirlo para mejorar tu concentración.", R.drawable.icono_ruido);
            }

            if (data.getTemperatura() < THRESHOLD_TEMPERATURA_MIN || data.getTemperatura() > THRESHOLD_TEMPERATURA_MAX) {
                lanzarNotificacion(context, "Temperatura", "La temperatura no es óptima para trabajar cómodamente.", R.drawable.icono_temperatura);
            }

            if (data.getHidratacion() < THRESHOLD_HIDRATACION) {
                lanzarNotificacion(context, "Hidratación", "Recuerda mantenerte hidratado. Bebe agua.", R.drawable.icono_hidratacion);
            }

        } else {
            Log.d("NotificationWorker", "No hay datos para verificar");
        }
    }

    private void lanzarNotificacion(Context context, String titulo, String mensaje, int icono) {
        NotificacionesFirebase notificacion = new NotificacionesFirebase(context, titulo, mensaje, "Aviso", icono, null);
        notificacion.lanzarNotificacion();
    }


}