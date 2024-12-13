package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Build;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.sedora.R;
import com.example.sedora.model.Meta;
import com.example.sedora.presentation.adapters.MetaAdapter;
import com.example.sedora.presentation.managers.FirebaseHelper;
import com.example.sedora.presentation.managers.MetasManager;

import android.Manifest;
import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.presentation.managers.Popup_pantalla_inicio;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PantallaInicioActivity extends AppCompatActivity implements MqttCallback{

    private static final String TAG = "PantallaInicio";
    private static final String BROKER = "tcp://broker.hivemq.com:1883"; // Broker WebSocket
    private static final String TOPIC_LED = "Sedora/led/status"; // Tópico para el LED
    private static final int QOS = 1; // Calidad de servicio para MQTT
    private static final String clientId = "sedoraapp" + System.currentTimeMillis();


    private MqttClient client;
    private MqttConnectOptions connOpts;
    private boolean isLedOn = false; // Estado inicial del LED
    private TextView connectionStatus; // Muestra el estado del LED
    private ImageView ledButton; // Botón para controlar el LED

    private TextView textView19;
    private TextView textHumidity;
    private TextView textView18;
    private TextView textView17;

    private HalfDonutChart halfDonutChart;
    private TextView heading12; // Para mostrar la puntuación numérica
    private TextView heading11; // Para mostrar el texto del estado
    private FirebaseHelper firebaseHelper;
    private FirebaseUser currentUser;


    private TextView textView6;
    private TextView textView5;
    private RecyclerView recyclerMetaActual;
    private MetaAdapter metaActualAdapter;
    private List<Meta> metaActualList;
    private FirebaseFirestore db;

    TextView metaTitulo, metaDescripcion;
    ProgressBar metaProgresoBar;
    ImageView metaIcono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);


        // Inicializar vistas
        connectionStatus = findViewById(R.id.textView8);
        ledButton = findViewById(R.id.imageView13);

        // Configurar MQTT
        setupMQTT();

        // Configurar botón para encender/apagar el LED
        ledButton.setOnClickListener(v -> toggleLed());


        // Obtén el Header
        // Inicializar Firebase y obtener usuario actual
        firebaseHelper = new FirebaseHelper();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Inicializar referencias a los elementos de la interfaz
        halfDonutChart = findViewById(R.id.halfDonutChart);
        heading12 = findViewById(R.id.heading12);
        heading11 = findViewById(R.id.heading11);

        textView17 = findViewById(R.id.textView17);
        textView18 = findViewById(R.id.textView18);
        textView19 = findViewById(R.id.textView19);
        textView6 = findViewById(R.id.textView6);
        textView5 = findViewById(R.id.textView5);

        // Configurar Header y otras funcionalidades
        configurarHeader();
        configurarPantallaInicio();
        configurarMenu();

        if (currentUser != null) {
            calcularPuntuacionDiaria();
            //obtenerUltimaTomaDelDia();
            mostrarConsejoDelDia();
            calcularTiempoSentado();
        }

        // prueba para lanzar una noti
//        NotificacionesFirebase noti = new NotificacionesFirebase(getApplicationContext(), "Título", "Descripción", "Descripción larga", R.drawable.sedora_logo, MainActivity.class);
//        noti.lanzarNotificacion();
        // Inicialización del botón

        try {
            client = new MqttClient(BROKER, clientId, new MemoryPersistence());

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill("Sedora/desconectado", "Desconectada!".getBytes(), 0, false); // Mensaje de última voluntad

            client.connect(connOpts);

            client.setCallback(this);
            client.subscribe("Sedora/sensores/temperatura", QOS);
            client.subscribe("Sedora/sensores/humedad", QOS);
            client.subscribe("Sedora/sensores/sonido", QOS);
            client.subscribe("Sedora/sensores/luz", QOS);
            Log.i("MQTT", "Suscripción a los tópicos de sensores");

        } catch (MqttException e) {
            Log.e("MQTT", "Error al conectar con el bróker: " + e.getMessage());
        }

        if (textView19 == null || textHumidity == null || textView17 == null || textView18 == null) {
            Log.e(TAG, "Uno o más TextView no fueron inicializados correctamente.");
        }
    }

    private void setupMQTT() {
        try {
            String clientId = MqttClient.generateClientId();
            client = new MqttClient(BROKER, clientId, new MemoryPersistence());
            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Opcional: establece un tiempo de espera para la conexión
            connOpts.setConnectionTimeout(10);

            // Intenta conectar
            Log.i(TAG, "Conectando al broker " + BROKER);
            client.setCallback(this);
            client.connect(connOpts);

            publishLedStatus();
            Log.i(TAG, "Conectado al broker");
        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar al broker MQTT", e);
        }
    }



    private void toggleLed() {
        try {
            isLedOn = !isLedOn;
            publishLedStatus();
            updateLedStatus();
            Log.i(TAG, "Estado del LED cambiado: " + (isLedOn ? "ON" : "OFF"));
        } catch (Exception e) {
            Log.e(TAG, "Error al cambiar estado del LED", e);
            connectionStatus.setText("Error al cambiar estado del LED");
        }
    }

    private void publishLedStatus() throws MqttException {
        String message = isLedOn ? "ON" : "OFF";
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(QOS);
        client.publish(TOPIC_LED, mqttMessage);

        Log.i(TAG, "Mensaje publicado al broker: " + message);
    }

    private void updateLedStatus() {
        connectionStatus.setText(isLedOn ? "Encendido" : "Apagado");
    }

    private void configurarHeader() {
        // Solicitar permiso de notificaciones
        requestNotificationPermission();

        //---INICIO HEADER---
        Header header = findViewById(R.id.header);
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Inicio");
        //---FIN HEADER---

//        NotificacionManager notificacionManager = new NotificacionManager();
//        boolean hasNotifications = !notificacionManager.getNotificaciones().isEmpty();
//        header.updateNotificationIcon(hasNotifications);

        Popup_pantalla_inicio popupPantallaInicio = new Popup_pantalla_inicio(this, this);
        popupPantallaInicio.setupPopup();
    }

    private void configurarPantallaInicio() {
        ImageView imageView21 = findViewById(R.id.imageView21);
        imageView21.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicioActivity.this, TusMetasActivity.class);
            startActivity(intent);
        });
    }

    private void configurarMenu() {
        //FUNCIONALIDAD BOTONES MENU
        MenuManager funcionMenu = new MenuManager();

        ImageView btnPantallaPrincipal = findViewById(R.id.btnHome);
        btnPantallaPrincipal.setOnClickListener(v -> funcionMenu.abrirPantallaInicio(PantallaInicioActivity.this));

        ImageView btnPantallaProgreso = findViewById(R.id.btnProgreso);
        btnPantallaProgreso.setOnClickListener(v -> funcionMenu.abrirPantallaProgreso(PantallaInicioActivity.this));

        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(v -> funcionMenu.abrirPantallaAjustes(PantallaInicioActivity.this));

        ImageView btnPantallaPerfil = findViewById(R.id.Perfil);
        btnPantallaPerfil.setOnClickListener(v -> funcionMenu.abrirPantallaPerfil(PantallaInicioActivity.this));
    }



    private void calcularTiempoSentado() {
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        firebaseHelper.db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("Datos")
                .document(fechaHoy)
                .collection("Tomas")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        long tiempoBuenaPostura = 0;
                        long tiempoMalaPostura = 0;
                        long tiempoAnterior = 0;

                        for (DocumentSnapshot document : task.getResult()) {
                            Double presion1 = document.getDouble("presion1");
                            Double presion2 = document.getDouble("presion2");
                            Timestamp timestamp = document.getTimestamp("timestamp");

                            if (timestamp != null) {
                                long tiempoActual = timestamp.toDate().getTime();

                                if (tiempoAnterior != 0) {
                                    long intervalo = tiempoActual - tiempoAnterior;

                                    if (presion1 != null && presion2 != null) {
                                        if (presion1 > 0 && presion2 > 0) {
                                            tiempoBuenaPostura += intervalo;
                                        } else {
                                            tiempoMalaPostura += intervalo;
                                        }
                                    }
                                }

                                tiempoAnterior = tiempoActual;
                            }
                        }

                        // Convertir los tiempos a horas y minutos
                        long horasBuenaPostura = tiempoBuenaPostura / (1000 * 60 * 60);
                        long minutosBuenaPostura = (tiempoBuenaPostura / (1000 * 60)) % 60;
                        long horasMalaPostura = tiempoMalaPostura / (1000 * 60 * 60);
                        long minutosMalaPostura = (tiempoMalaPostura / (1000 * 60)) % 60;

                        // Actualizar los TextView con los resultados
                        textView6.setText(String.format("%d horas %d minutos", horasBuenaPostura, minutosBuenaPostura));
                        textView5.setText(String.format("%d horas %d minutos", horasMalaPostura, minutosMalaPostura));
                    } else {
                        // Manejar el caso en que no hay tomas para el día actual
                        textView6.setText("0 horas 0 minutos");
                        textView5.setText("0 horas 0 minutos");
                    }
                });
    }

    private void mostrarConsejoDelDia() {
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        firebaseHelper.db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("Datos")
                .document(fechaHoy)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        String consejo = determinarConsejo(document);
                        TextView textView29 = findViewById(R.id.textView29);
                        textView29.setText(consejo);
                    } else {
                        // Manejar el caso en que no hay datos para el día actual
                        System.err.println("No hay datos disponibles para hoy.");
                        TextView textView29 = findViewById(R.id.textView29);
                        textView29.setText("No hay datos disponibles para el día de hoy.");
                    }
                });
    }

    private String determinarConsejo(DocumentSnapshot document) {
        Double luminosidadPromedio = document.getDouble("luminosidadPromedio");
        Double ruidoPromedio = document.getDouble("ruidoPromedio");
        Double presion1Promedio = document.getDouble("presion1Promedio");
        Double presion2Promedio = document.getDouble("presion2Promedio");
        Double temperaturaPromedio = document.getDouble("temperaturaPromedio");
        Double humedadPromedio = document.getDouble("humedadPromedio");
        Double proximidadPromedio = document.getDouble("proximidadPromedio");

        double maxDesviacion = 0;
        String consejo = "";

        if (luminosidadPromedio != null && Math.abs(luminosidadPromedio - 300) > maxDesviacion) {
            maxDesviacion = Math.abs(luminosidadPromedio - 300);
            consejo = "Es recomendable que incrementes el nivel de luminosidad en tu habitación.";
        }
        if (ruidoPromedio != null && Math.abs(ruidoPromedio - 45) > maxDesviacion) {
            maxDesviacion = Math.abs(ruidoPromedio - 45);
            consejo = "Es recomendable que trates de disminuir el nivel de ruido en tu ambiente.";
        }
        if (presion1Promedio != null && presion2Promedio != null && (presion1Promedio == 0 || presion2Promedio == 0)) {
            consejo = "Es recomendable que ajustes tu postura para mejorar la presión en los sensores.";
        }
        if (temperaturaPromedio != null && Math.abs(temperaturaPromedio - 22) > maxDesviacion) {
            maxDesviacion = Math.abs(temperaturaPromedio - 22);
            consejo = "Es recomendable que ajustes la temperatura en tu habitación.";
        }
        if (humedadPromedio != null && Math.abs(humedadPromedio - 40) > maxDesviacion) {
            maxDesviacion = Math.abs(humedadPromedio - 40);
            consejo = "Es recomendable que ajustes la humedad en tu entorno.";
        }
        if (proximidadPromedio != null && Math.abs(proximidadPromedio - 1) > maxDesviacion) {
            maxDesviacion = Math.abs(proximidadPromedio - 1);
            consejo = "Es recomendable que ajustes la proximidad en tu entorno.";
        }

        return consejo;
    }


    private void calcularPuntuacionDiaria() {
        firebaseHelper.calcularPuntuacionDiaria(currentUser, puntuacion -> {
            // Actualizar la interfaz con la puntuación calculada
            halfDonutChart.setScorePercentage(puntuacion);
            heading12.setText(String.valueOf((int) puntuacion));

            if (puntuacion >= 80) {
                heading11.setText("Excelente");
            } else if (puntuacion >= 50) {
                heading11.setText("Bueno");
            } else {
                heading11.setText("Mejorable");
            }
        });
    }
    // Solicitar permiso de notificaciones
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Verifica si el permiso ya está concedido
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Solicita el permiso
                Log.d("Permiso", "Solicitando permiso de notificaciones...");
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                Log.d("Permiso", "Permiso de notificaciones ya concedido.");
            }
        } else {
            Log.d("Permiso", "No se requiere POST_NOTIFICATIONS en esta versión de Android.");
        }
    }

    // Manejar la respuesta del usuario al solicitar el permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permiso", "Permiso de notificaciones concedido.");
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Permiso", "Permiso de notificaciones denegado.");
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "Conexión perdida", cause);
        connectionStatus.setText("Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.i(TAG, "Mensaje recibido en tópico: " + topic);
        String payload = new String(message.getPayload());
        Log.i(TAG, "Contenido del mensaje recibido: " + payload);

        // Manejar mensajes del tópico del LED
        if (topic.equals(TOPIC_LED)) {
            if (payload.equalsIgnoreCase("ON")) {
                isLedOn = true;
            } else if (payload.equalsIgnoreCase("OFF")) {
                isLedOn = false;
            }
            updateLedStatus(); // Actualizar la interfaz
        }
        runOnUiThread(() -> {
            try {
                if (topic.equals("Sedora/sensores/temperatura")) {
                    float temperatura = Float.parseFloat(payload);
                    if (textView19 != null) {
                        textView19.setText(temperatura + " °C");
                    }
                } else if (topic.equals("Sedora/sensores/humedad")) {
                    float humedad = Float.parseFloat(payload);
                    if (textHumidity != null) {
                        textHumidity.setText( humedad + " %");
                    }
                } else if (topic.equals("Sedora/sensores/sonido")) {
                    int sonido = Integer.parseInt(payload);
                    if (textView17 != null) {
                        String estadoSonido = (sonido == 1) ? "Inadecuado" : "Adecuado";
                        textView17.setText( estadoSonido);
                    }
                } else if (topic.equals("Sedora/sensores/luz")) {
                    int luz = Integer.parseInt(payload);
                    if (textView18 != null) {
                        String estadoLuz = (luz == 1) ? "Inadecuada" : "Adecuada";
                        textView18.setText(estadoLuz);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al procesar el mensaje: " + payload, e);
            }
        });

    }

    /**
     * Convierte un String a float, lanza una excepción si no es válido.
     */
    private float parseToFloat(String value) throws NumberFormatException {
        if (value == null || value.equalsIgnoreCase("nan")) {
            throw new NumberFormatException("Valor no válido: " + value);
        }
        return Float.parseFloat(value);
    }

    /**
     * Convierte un String a int, lanza una excepción si no es válido.
     */
    private int parseToInt(String value) throws NumberFormatException {
        if (value == null || value.equalsIgnoreCase("nan")) {
            throw new NumberFormatException("Valor no válido: " + value);
        }
        return Integer.parseInt(value);
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(TAG, "Entrega completada");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                Log.i(TAG, "Desconectado del broker MQTT");
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error al desconectar del broker MQTT", e);
        }
    }


}

