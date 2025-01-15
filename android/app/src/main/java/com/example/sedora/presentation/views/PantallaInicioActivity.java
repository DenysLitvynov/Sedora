package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Build;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.sedora.R;
import com.example.sedora.model.EstadoMeta;
import com.example.sedora.model.Meta;
import com.example.sedora.model.MetaUsuario;
import com.example.sedora.model.SensorData;
import com.example.sedora.presentation.adapters.MetaUsuarioAdapter;
import com.example.sedora.presentation.managers.FirebaseHelper;
import com.example.sedora.presentation.managers.MetasManager;

import android.Manifest;
import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.presentation.managers.PopUp;
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
import java.util.HashMap;
import java.util.List;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

public class PantallaInicioActivity extends AppCompatActivity implements MqttCallback {

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
    private MetaUsuarioAdapter metaActualAdapter;
    private FirebaseFirestore db;
    private MetasManager metasManager;
    private MetaUsuario metaActual;

    private ArrayList<SensorData> sensorsData;
    private List<String> notificaciones = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);

        // Inicializar Firebase y lista de datos
        db = FirebaseFirestore.getInstance();

        metasManager = new MetasManager();

        // Inicializar Firebase y obtener usuario actual
        firebaseHelper = new FirebaseHelper();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();



        // Inicializar RecyclerView
        recyclerMetaActual = findViewById(R.id.recyclerViewMetaActual);
        recyclerMetaActual.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar vistas
        connectionStatus = findViewById(R.id.textView8);
        ledButton = findViewById(R.id.imageView13);

        // Configurar MQTT
        setupMQTT();

        // Configurar botón para encender/apagar el LED
        ledButton.setOnClickListener(v -> toggleLed());

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
            mostrarConsejoDelDia();
            calcularTiempoSentado();
        }

        // Botón Popup Informe:
        ImageView btnInformeDiario = findViewById(R.id.iconoAbrirInforme);
        btnInformeDiario.setOnClickListener(v -> {
            PopUp popupInformeDiario = new PopUp(this, R.layout.popup_informe_diario);
            popupInformeDiario.setupBotonCerrar(R.id.cerrar_popup);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                String path = "/carpeta/Informes/" + userId + "/fichero.txt";

                StorageReference fileRef = FirebaseStorage.getInstance().getReference(path);

                fileRef.getBytes(1024 * 1024)
                        .addOnSuccessListener(bytes -> {
                            String contenido = new String(bytes, StandardCharsets.UTF_8);
                            TextView popupContenido = popupInformeDiario.getDialog().findViewById(R.id.contenido_popup);
                            popupContenido.setText(contenido);
                            popupInformeDiario.enseñarPopUp();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirebaseStorage", "Error al descargar informe.txt", e);
                            Toast.makeText(this, "Error al cargar el informe.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show();
            }
        });

        sensorsData = new ArrayList<>();
        getSensors();
        getNotificaciones();
        comprobarMetasUsuario();
    }


    public void comprobarMetasUsuario() {
        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("metasUsuario")
                .get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result == null || result.isEmpty()) {
                            List<Meta> listaMetas = new ArrayList<>();
                            db.collection("metas")
                                    .get()
                                    .addOnCompleteListener(task2-> {
                                        if (task2.isSuccessful() && task2.getResult() != null && !task2.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : task2.getResult()) {
                                                Meta meta = document.toObject(Meta.class);
                                                listaMetas.add(meta);
                                            }
                                            for (Meta meta : listaMetas) {
                                                db.collection("usuarios")
                                                        .document(currentUser.getUid())
                                                        .collection("metasUsuario")
                                                        .document(String.valueOf(meta.getNumeroMeta())).set(new MetaUsuario(meta))
                                                        .addOnSuccessListener(docRef -> {
                                                            System.out.println("Meta añadida con ID: " + docRef);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            System.err.println("Error al guardar meta: " + e.getMessage());
                                                        });
                                            }
                                        } else {
                                            System.err.println("No se encontraron metas en la colección 'metas'.");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        System.err.println("Error al recuperar metas: " + e.getMessage());
                                    });
                        } else {
                            Log.d("DEBUG_METAS", "El usuario ya tiene metas. Cantidad: " + result.size());
                        }
                    } else {
                        Log.e("DEBUG_METAS", "Error al comprobar metas: ", task.getException());
                    }
                });
    }

    private boolean comprobarPresion() {
        long total = sensorsData.stream().filter(sensorData -> sensorData.getPresion1() >= 1 && sensorData.getPresion2() >= 1).count();
        return total == sensorsData.size();
    }

    private boolean comprobarLuz() {
        long total = sensorsData.stream().filter(sensorData -> sensorData.getIluminacion() >= 500 && sensorData.getIluminacion() <= 1000).count();
        return total == sensorsData.size();
    }

    private boolean comprobarProximidad() {
        long total = sensorsData.stream().filter(sensorData -> sensorData.getIluminacion() >= 500 && sensorData.getIluminacion() <= 1000).count();
        return total == sensorsData.size();
    }

    private boolean comprobarNotificacionesIluminacion() {

        for (String titulo : notificaciones) {
            if (titulo.equalsIgnoreCase("Iluminación")) {
                return false;
            }
        }
        return true;
    }

    private boolean comprobarNotificacionesPostura() {

        for (String titulo : notificaciones) {
            if (titulo.equalsIgnoreCase("Postura")) {
                return false;
            }
        }
        return true;
    }

    private boolean comprobarNotificacionesDistanciaMonitor() {

        for (String titulo : notificaciones) {
            if (titulo.equalsIgnoreCase("Distancia al monitor")) {
                return false;
            }
        }
        return true;
    }

    private boolean comprobarNotificacionesEstiramientos() {

        for (String titulo : notificaciones) {
            if (titulo.equalsIgnoreCase("Estiramientos")) {
                return false;
            }
        }
        return true;
    }
    private boolean comprobarNotificacionesDescanso() {

        for (String titulo : notificaciones) {
            if (titulo.equalsIgnoreCase("Descanso")) {
                return false;
            }
        }
        return true;
    }

    private void getSensors() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(new Date());
        Log.d("DATE",date);
        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("Datos")
                .document(date)
                .collection("Tomas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {

                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                SensorData sensorData = document.toObject(SensorData.class);
                                if (sensorData != null) {
                                    sensorsData.add(sensorData);
                                }
                            }
                        } else {
                            Log.d("Firestore", "No se encontraron documentos en la subcolección Tomas");

                        }
                    } else {
                        Log.e("Firestore", "Error al cargar las metas actuales", task.getException());

                    }
                });

    }

    private void getNotificaciones() {
        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("notificaciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Procesa cada documento encontrado
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String titulo = document.getString("titulo");
                                if (titulo != null) {
                                    notificaciones.add(titulo); // Agregar el título de la notificación a la lista
                                }
                            }
                            Log.d("Firestore", "Notificaciones cargadas correctamente");
                        } else {
                            Log.d("Firestore", "No se encontraron documentos en la colección notificaciones.");
                        }
                    } else {
                        Log.e("Firestore", "Error al cargar las notificaciones", task.getException());
                    }
                });
    }

    private void setupMQTT() {
        new Thread(() -> {
            try {
                String clientId = MqttClient.generateClientId();
                client = new MqttClient(BROKER, clientId, new MemoryPersistence());
                connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                connOpts.setConnectionTimeout(10); // Tiempo de espera de 10 segundos
                connOpts.setAutomaticReconnect(true);

                Log.i(TAG, "Conectando al broker " + BROKER);
                client.setCallback(this);
                client.connect(connOpts);

                // Suscripciones si la conexión es exitosa
                client.subscribe("Sedora/sensores/temperatura", QOS);
                client.subscribe("Sedora/sensores/humedad", QOS);
                client.subscribe("Sedora/sensores/ruido", QOS);
                client.subscribe("Sedora/sensores/luminosidad", QOS);
                Log.i(TAG, "Conectado al broker y suscrito a los tópicos.");

            } catch (MqttException e) {
                Log.e(TAG, "Error al conectar al broker MQTT: " + e.getMessage());
            }
        }).start();
    }

    private void toggleLed() {
        try {
            isLedOn = !isLedOn;
            publishLedStatus();
            updateLedStatus();
            Log.i(TAG, "Estado del LED cambiado: " + (isLedOn ? "ON" : "OFF"));
        } catch (Exception e) {
            Log.e(TAG, "Error al cambiar estado del LED", e);
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

    private void cargarMetaActual() {

        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("metasUsuario")
                .whereEqualTo("estadoMeta", EstadoMeta.ACTUAL.name())
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document= querySnapshot.getDocuments().get(0);
                            MetaUsuario metaUsuario = document.toObject(MetaUsuario.class);
                            if (metaUsuario != null) {
                                metaActual = metaUsuario;
                                metaActualAdapter = new MetaUsuarioAdapter(this, List.of(metaActual), 0);
                                recyclerMetaActual.setAdapter(metaActualAdapter);
                                metaActualAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(this, "No hay metas actuales disponibles.", Toast.LENGTH_SHORT).show();
                            Meta meta = new Meta("Mantente correcto 1 h", "Mantén una posición correcta durante 1 hora.","icono_sentado", "01");
                            metaActual = new MetaUsuario(meta);
                            metaActualAdapter = new MetaUsuarioAdapter(this, List.of(metaActual), 0);
                            recyclerMetaActual.setAdapter(metaActualAdapter);
                            metaActualAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Error al cargar las metas actuales.", Toast.LENGTH_SHORT).show();
                    }
                });

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
                        textHumidity.setText(humedad + " %");
                    }
                } else if (topic.equals("Sedora/sensores/ruido")) {
                    int sonido = Integer.parseInt(payload);
                    if (textView18 != null) {
                        String estadoSonido = (sonido == 0) ? "Inadecuado" : "Adecuado";
                        textView18.setText(estadoSonido);
                    }
                } else if (topic.equals("Sedora/sensores/luminosidad")) {
                    int luz = Integer.parseInt(payload);
                    if (textView17 != null) {
                        String estadoLuz = (luz == 0) ? "Inadecuada" : "Adecuada";
                        textView17.setText(estadoLuz);
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

    public void actualizarMeta(MetaUsuario metaUsuario, EstadoMeta estadoMeta, int porcentaje){

        Map<String, Object> updates = new HashMap<>();
        updates.put("estadoMeta", estadoMeta.name());
        updates.put("porcentajeActual", porcentaje);

        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("metasUsuario")
                .document(metaUsuario.getMeta().getNumeroMeta())
                .update(updates)
                .addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        System.out.println("Meta de usuario actualizada correctamente.");
                    } else {
                        System.out.println("Error al actualizar la meta de usuario: " + updateTask.getException());
                    }
                });
    }

    public void nuevaMetaActual(String numeroMeta){

        int number = Integer.parseInt(numeroMeta);
        number += 1;

        String  siguienteMetaUsuario= String.format("%02d", number);

        Map<String, Object> updates = new HashMap<>();
        updates.put("estadoMeta", EstadoMeta.ACTUAL);

        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("metasUsuario")
                .document(siguienteMetaUsuario)
                .update(updates)
                .addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        System.out.println("Meta de usuario actualizada correctamente.");
                    } else {
                        System.out.println("Error al actualizar la meta de usuario: " + updateTask.getException());
                    }
                });
    }


    public void pasarMetaActualAConseguida() {

        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("metasUsuario")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        MetaUsuario metaUsuario = null;
                        for (DocumentSnapshot document : querySnapshot) {
                            metaUsuario = document.toObject(MetaUsuario.class);

                            if (EstadoMeta.ACTUAL.name().equals(metaUsuario.getEstadoMeta().name())) {

                                metaUsuario.setEstadoMeta(EstadoMeta.COMPLETADO);
                                metaUsuario.setPorcentajeActual(100);
                                actualizarMeta(metaUsuario,EstadoMeta.COMPLETADO, 100);
                                nuevaMetaActual(metaUsuario.getMeta().getNumeroMeta());
                            }
                        }

                    } else {
                        System.out.println("Error al obtener metas: "+ task.getException());
                    }
                });
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



    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PASO","3");
        checkConfirmarMetaAPasada();
    }

    private void checkConfirmarMetaAPasada() {
        cargarMetaActual();
        if(metaActual != null){
            if(sensorsData!=null && !sensorsData.isEmpty()){
                switch (metaActual.getMeta().getNumeroMeta()){
                    case "01":
                        long actualizar = sensorsData.stream().filter(sensorData -> sensorData.getPresion1() >= 1 && sensorData.getPresion2() >=1).count();
                        if(sensorsData.size() == actualizar){
                            actualizarMeta(metaActual,EstadoMeta.COMPLETADO,100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                        }
                        break;
                    case "02":
                        long actualizar2 = sensorsData.stream()
                                .filter(sensorData -> sensorData.getPresion1() >= 1 && sensorData.getPresion1() <= 2 &&
                                        sensorData.getPresion2() >= 1 && sensorData.getPresion2() <= 2)
                                .count();
                        if (actualizar2 >= 1) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                        }
                        break;
                    case "03":
                        long actualizar3 = sensorsData.stream().filter(sensorData -> sensorData.getPresion1() >= 1 && sensorData.getPresion2() >=1).count();
                        if(sensorsData.size() == actualizar3){
                            actualizarMeta(metaActual,EstadoMeta.COMPLETADO,100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                        }
                        break;
                    case "04":
                        getNotificaciones();
                        if (comprobarNotificacionesIluminacion()) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta 04", "Meta completada: No se encontraron notificaciones con 'iluminación'.");
                        } else {
                            Log.d("Meta 04", "Meta no completada: Se encontró una notificación con 'iluminación'.");
                        }
                        break;
                    case "05":
                        getNotificaciones();
                        if (comprobarNotificacionesIluminacion()) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta 05", "Meta completada: No se encontraron notificaciones con 'iluminación'.");
                        } else {
                            Log.d("Meta 05", "Meta no completada: Se encontró una notificación con 'iluminación'.");
                        }
                        break;
                    case "06":
                        getNotificaciones();
                        if (comprobarNotificacionesPostura()) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta 06", "Meta completada: No se encontraron notificaciones con 'postura'.");
                        } else {
                            Log.d("Meta 06", "Meta no completada: Se encontró una notificación con 'postura'.");
                        }
                        break;
                    case "07":
                        long actualizar7 = sensorsData.stream()
                                .filter(sensorData -> sensorData.getPresion1() >= 0 && sensorData.getPresion1() <= 1 &&
                                        sensorData.getPresion2() >= 0 && sensorData.getPresion2() <= 1)
                                .count();
                        if (actualizar7 >= 3) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                        }
                        break;
                    case "08":
                        long actualizar8 = sensorsData.stream().filter(sensorData -> sensorData.getPresion1() >= 1 && sensorData.getPresion2() >=1).count();
                        if(sensorsData.size() == actualizar8){
                            actualizarMeta(metaActual,EstadoMeta.COMPLETADO,100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                        }
                        break;
                    case "09":
                        getNotificaciones();
                        if (comprobarNotificacionesIluminacion()) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta 09", "Meta completada: No se encontraron notificaciones con 'iluminación'.");
                        } else {
                            Log.d("Meta 09", "Meta no completada: Se encontró una notificación con 'iluminación'.");
                        }
                        break;
                    case "10":
                        getNotificaciones();
                        if (comprobarNotificacionesDistanciaMonitor()) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta 10", "Meta completada: No se encontraron notificaciones con 'distancia al monitor'.");
                        } else {
                            Log.d("Meta 10", "Meta no completada: Se encontró una notificación con 'distancia al monitor'.");
                        }
                        break;
                    case "11":
                        long transiciones = IntStream.range(1, sensorsData.size())
                                .filter(i -> {
                                    SensorData anterior = sensorsData.get(i - 1);
                                    SensorData actual = sensorsData.get(i);

                                    boolean posturaIncorrecta = anterior.getPresion1() >= 0 && anterior.getPresion1() < 1 &&
                                            anterior.getPresion2() >= 0 && anterior.getPresion2() < 1;

                                    boolean posturaCorrecta = actual.getPresion1() >= 1 && actual.getPresion2() >= 1;

                                    return posturaIncorrecta && posturaCorrecta;
                                })
                                .count();
                        if (transiciones >= 20) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Postura", "Meta completada: Se detectaron " + transiciones + " correcciones de postura.");
                        } else {
                            Log.d("Meta Postura", "Meta no completada: Solo se detectaron " + transiciones + " correcciones de postura.");
                        }
                        break;
                    case "12":
                        long notificacionesEstiramientos = notificaciones.stream()
                                .filter(titulo -> titulo.equalsIgnoreCase("Estiramientos"))
                                .count();
                        long transicionesPausa = IntStream.range(1, sensorsData.size())
                                .filter(i -> {
                                    SensorData anterior = sensorsData.get(i - 1);
                                    SensorData actual = sensorsData.get(i);
                                    boolean posturaActiva = anterior.getPresion1() >= 1 && anterior.getPresion2() >= 1;
                                    boolean posturaRelajada = actual.getPresion1() >= 0 && actual.getPresion1() < 1 &&
                                            actual.getPresion2() >= 0 && actual.getPresion2() < 1;
                                    return posturaActiva && posturaRelajada;
                                })
                                .count();
                        if (notificacionesEstiramientos >= 2 || transicionesPausa >= 2) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Pausas", "Meta completada: Se detectaron suficientes pausas de trabajo.");
                        } else {
                            Log.d("Meta Pausas", "Meta no completada: Solo se detectaron " +
                                    notificacionesEstiramientos + " notificaciones y " +
                                    transicionesPausa + " transiciones de postura.");
                        }
                        break;
                    case "13":
                        long alertasPosturaIncorrecta = notificaciones.stream()
                                .filter(titulo -> titulo.equalsIgnoreCase("Postura Incorrecta"))
                                .count();
                        if (alertasPosturaIncorrecta < 5) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Postura", "Meta completada: Se detectaron menos de 5 alertas de postura incorrecta.");
                        } else {
                            Log.d("Meta Postura", "Meta no completada: Se detectaron " + alertasPosturaIncorrecta + " alertas de postura incorrecta.");
                        }
                        break;
                    case "14":
                        long transicionesProximidad = IntStream.range(1, sensorsData.size())
                                .filter(i -> {
                                    SensorData anterior = sensorsData.get(i - 1);
                                    SensorData actual = sensorsData.get(i);
                                    boolean proximidadAdecuadaAnterior = anterior.getProximidad() >= 500 && anterior.getProximidad() <= 1000;
                                    boolean proximidadInadecuadaActual = actual.getProximidad() < 500 || actual.getProximidad() > 1000;
                                    return proximidadAdecuadaAnterior && proximidadInadecuadaActual;
                                })
                                .count();

                        if (transicionesProximidad >= 2) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Proximidad", "Meta completada: Se detectaron suficientes ajustes de proximidad.");
                        } else {
                            Log.d("Meta Proximidad", "Meta no completada: Solo se detectaron " +
                                    transicionesProximidad + " transiciones de proximidad.");
                        }
                        break;
                    case "15":
                        long levantarseTransiciones = IntStream.range(1, sensorsData.size())
                                .filter(i -> {
                                    SensorData anterior = sensorsData.get(i - 1);
                                    SensorData actual = sensorsData.get(i);
                                    boolean sentado = anterior.getPresion1() >= 1 && anterior.getPresion2() >= 1;
                                    boolean levantado = actual.getPresion1() < 1 && actual.getPresion2() < 1;
                                    return sentado && levantado;
                                })
                                .count();

                        if (levantarseTransiciones >= 8) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Levantarse", "Meta completada: Te levantaste de la silla al menos 8 veces en un día.");
                        } else {
                            Log.d("Meta Levantarse", "Meta no completada: Solo te levantaste " +
                                    levantarseTransiciones + " veces de la silla.");
                        }
                        break;
                    case "16":
                        boolean alertaDetectada = IntStream.range(1, sensorsData.size())
                                .anyMatch(i -> {
                                    SensorData anterior = sensorsData.get(i - 1);
                                    SensorData actual = sensorsData.get(i);
                                    return anterior.getPresion2() >= 1 && actual.getPresion2() < 1;
                                });

                        if (!alertaDetectada) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Espalda Recta", "Meta completada: Mantuvista la espalda recta durante todo el periodo sin alertas de corrección.");
                        } else {
                            Log.d("Meta Espalda Recta", "Meta no completada: Se detectaron alertas de corrección en la postura.");
                        }
                        break;
                    case "17":
                        boolean alertaDetectada2 = IntStream.range(1, sensorsData.size())
                                .anyMatch(i -> {
                                    SensorData anterior = sensorsData.get(i - 1);
                                    SensorData actual = sensorsData.get(i);

                                    // Condición de alerta de postura: Presión en la espalda (presion1) incorrecta
                                    boolean alertaPostura = anterior.getPresion1() >= 1 && actual.getPresion1() < 1;

                                    // Condición de alerta de luz: Iluminación fuera del rango adecuado
                                    boolean alertaLuz = actual.getIluminacion() < 500 || actual.getIluminacion() > 1000;

                                    return alertaPostura || alertaLuz;
                                });

                        if (!alertaDetectada2) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Sin Alertas", "Meta completada: No se detectaron alertas de postura ni de luz.");
                        } else {
                            Log.d("Meta Sin Alertas", "Meta no completada: Se detectaron alertas de postura o luz.");
                        }
                        break;
                    case "18":
                        boolean alertaDetectada3 = sensorsData.stream()
                                .anyMatch(data -> data.getPresion1() < 1);

                        if (!alertaDetectada3) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Jornada Postura", "Meta completada: Mantuvista una postura correcta durante toda la jornada laboral.");
                        } else {
                            Log.d("Meta Jornada Postura", "Meta no completada: Se detectaron alertas de postura incorrecta durante la jornada laboral.");
                        }
                        break;
                    case "19":
                        boolean alertaDetectada4 = sensorsData.stream()
                                .anyMatch(data -> {
                                    // Condición de alerta de postura: Presión en la espalda (presion1) incorrecta
                                    boolean alertaPostura = data.getPresion1() < 1;

                                    // Condición de alerta de luz: Iluminación fuera del rango adecuado
                                    boolean alertaLuz = data.getIluminacion() < 500 || data.getIluminacion() > 1000;

                                    return alertaPostura || alertaLuz;
                                });

                        if (!alertaDetectada4) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Ambiente Correcto", "Meta completada: Se trabajó en un ambiente iluminado y con postura correcta.");
                        } else {
                            Log.d("Meta Ambiente Correcto", "Meta no completada: Se detectaron alertas de postura o iluminación inadecuada.");
                        }
                        break;
                    case "20":
                        boolean alertaDetectada5 = sensorsData.stream()
                                .anyMatch(data -> {
                                    return data.getPresion2() < 1;
                                });
                        if (!alertaDetectada5) {
                            actualizarMeta(metaActual, EstadoMeta.COMPLETADO, 100);
                            nuevaMetaActual(metaActual.getMeta().getNumeroMeta());
                            Log.d("Meta Espalda Recta", "Meta completada: Mantuvista la espalda recta y sin alertas de postura durante todo el periodo evaluado.");
                        } else {
                            Log.d("Meta Espalda Recta", "Meta no completada: Se detectaron alertas de postura durante el periodo evaluado.");
                        }
                        break;
                    default:
                        Log.d("ERROR CASOS METAS", "Hay un error en los casos para completar las metas.");
                        break;

                }

            }else{
                Toast.makeText(this, "Sensor data is null o vacio", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Meta actual es null", Toast.LENGTH_LONG).show();
        }
    }

}


