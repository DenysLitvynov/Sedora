package com.example.raspberry;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import io.grpc.LoadBalancerRegistry;
import io.grpc.internal.PickFirstLoadBalancerProvider;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class RaspberryFirebase implements MqttCallback {

    private static final String PATH_TO_CREDENTIALS = "./raspberryFirebase370c7.json";
    private static final String broker = "tcp://broker.hivemq.com:1883";
    private static final String clientId = "hugoapp" + System.currentTimeMillis();
    private static final String topic = "Sedora/#";
    private static final int qos = 1;
    public static String BUCKET = "sedora-gti.firebasestorage.app";
    public static Storage storage;
    public static String nombreFichero = UUID.randomUUID().toString();
    private MqttClient client;
    private Firestore db;

    // Variables para almacenar los datos recibidos
    private final Map<String, Object> sensorData = new HashMap<>();

    public static void main(String[] args) {
        new RaspberryFirebase().start();
        try {
            storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(
                            new FileInputStream(PATH_TO_CREDENTIALS)))
                    .build()
                    .getService();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Subir el archivo generado por ConvBot a Firebase Storage
        String archivoGenerado = "/home/pi/dlitvyn/fichero.txt"; // Ruta exacta del archivo en la Raspberry Pi
        String storagePath = "carpeta/Informes/k1gKUfsO2qXi1e3TJh5C7VbVFUS2/fichero.txt";
        subirFichero(archivoGenerado, storagePath);
    }



    static private void subirFichero(String fichero, String referencia) {
        try {
            BlobId blobId = BlobId.of(BUCKET, referencia);
            // Generar un token de acceso
            String token = UUID.randomUUID().toString();

            // Establecer el tipo de contenido y agregar el token
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType("text/plain")
                    .setMetadata(Map.of("firebaseStorageDownloadTokens", token))
                    .build();

            // Subir el archivo con los permisos adecuados
            storage.create(blobInfo, Files.readAllBytes(Paths.get(fichero)));

            // Proporcionar acceso público al archivo
            storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            System.out.println("Fichero subido: " + referencia);
            System.out.println("URL del fichero: https://firebasestorage.googleapis.com/v0/b/" + BUCKET + "/o/" + referencia.replace("/", "%2F") + "?alt=media&token=" + token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            // Registrar el balanceador 'pick_first' para evitar errores gRPC
            LoadBalancerRegistry.getDefaultRegistry()
                    .register(new PickFirstLoadBalancerProvider());

            // Inicializar Firebase
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(PATH_TO_CREDENTIALS)))
                    .build();
            FirebaseApp.initializeApp(options);

            db = FirestoreClient.getFirestore();
            System.out.println("Firebase inicializado correctamente.");

            // Configurar MQTT
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill("Sedora/desconectado", "Desconectada!".getBytes(), 0, false);

            client.setCallback(this);
            client.connect(connOpts);
            client.subscribe(topic, qos);
            System.out.println("Suscripción a los tópicos MQTT completada.");
        } catch (Exception e) {
            System.err.println("Error inicializando el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Conexión MQTT perdida...");
        if (cause != null) cause.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("Mensaje recibido en el tópico: " + topic + " - Payload: " + payload);

        try {
            switch (topic) {
                case "Sedora/sensores/temperatura":
                    sensorData.put("temperatura", Double.parseDouble(payload));
                    break;
                case "Sedora/sensores/humedad":
                    sensorData.put("humedad", Double.parseDouble(payload));
                    break;
                case "Sedora/sensores/luminosidad":
                    sensorData.put("luminosidad", Double.parseDouble(payload));
                    break;
                case "Sedora/sensores/presion1":
                    sensorData.put("presion1", Double.parseDouble(payload));
                    break;
                case "Sedora/sensores/presion2":
                    sensorData.put("presion2", Double.parseDouble(payload));
                    break;
                case "Sedora/sensores/proximidad":
                    sensorData.put("proximidad", Double.parseDouble(payload));
                    break;
                case "Sedora/sensores/ruido":
                    sensorData.put("ruido", Double.parseDouble(payload));
                    break;
                default:
                    System.out.println("Tópico desconocido: " + topic);
            }

            // Verificar si todos los datos necesarios están presentes
            if (sensorData.size() == 7) { // Sin incluir "timestamp"
                completarDatosFaltantes();
                subirDatosFirestore(new HashMap<>(sensorData));
                sensorData.clear(); // Limpiar para los próximos datos
            }
        } catch (Exception e) {
            System.err.println("Error procesando el mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Entrega completa!");
    }

    private void completarDatosFaltantes() {
        // Rellenar campos faltantes con valor predeterminado 0
        sensorData.putIfAbsent("temperatura", 0.0);
        sensorData.putIfAbsent("humedad", 0.0);
        sensorData.putIfAbsent("luminosidad", 0.0);
        sensorData.putIfAbsent("presion1", 0.0);
        sensorData.putIfAbsent("presion2", 0.0);
        sensorData.putIfAbsent("proximidad", 0.0);
        sensorData.putIfAbsent("ruido", 0.0);

        // Añadir la fecha y hora actual al mapa utilizando timestamp
        sensorData.put("timestamp", com.google.cloud.Timestamp.now());
    }

    private void subirDatosFirestore(Map<String, Object> data) {
        String userId = "k1gKUfsO2qXi1e3TJh5C7VbVFUS2";
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Referencia al documento "Datos" con la fecha de hoy
        DocumentReference datosDocRef = db.collection("usuarios")
                .document(userId)
                .collection("Datos")
                .document(fechaHoy);

        // Crear o actualizar el documento "Datos" si no existe
        ApiFuture<DocumentSnapshot> future = datosDocRef.get();
        try {
            DocumentSnapshot document = future.get();
            if (!document.exists()) {
                ApiFuture<WriteResult> writeResult = datosDocRef.set(new HashMap<>());
                System.out.println("Documento 'Datos' creado: " + writeResult.get().getUpdateTime());
            }

            // Generar un identificador único basado en la hora actual
            String docId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

            // Referencia a la subcolección "Tomas"
            DocumentReference tomasDocRef = datosDocRef.collection("Tomas")
                    .document(docId);

            // Subir los datos a la subcolección "Tomas"
            ApiFuture<WriteResult> result = tomasDocRef.set(data);
            System.out.println("Datos subidos a Firestore (nuevo documento): " + result.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error subiendo datos a Firestore: " + e.getMessage());
        }
    }
}
