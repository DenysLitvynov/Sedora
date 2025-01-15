package com.example.informe;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.grpc.LoadBalancerRegistry;
import io.grpc.internal.PickFirstLoadBalancerProvider;

public class ConvBot {

    private static final String PATH_TO_CREDENTIALS = "./raspberryFirebase370c7.json";
    private static final String FIRESTORE_COLLECTION = "usuarios";
    private static final String FIRESTORE_SUBCOLLECTION = "Datos";
    private static final String ENDPOINT = "https://api.poligpt.upv.es";
    private static final String API_KEY = ""; // poner la clave para que funcione (la quitamos por temas de seguridad a la hora de subir a git)
    private static final String MODEL = "llama3.3:70b";

    public static void main(String[] args) {
        // Registrar el balanceador 'pick_first' para evitar errores gRPC
        LoadBalancerRegistry.getDefaultRegistry()
                .register(new PickFirstLoadBalancerProvider());

        Firestore db = initializeFirebase();
        if (db == null) {
            System.err.println("No se pudo inicializar Firebase. Programa terminado.");
            return;
        }

        String userId = "PVd8Ci0fTtc9QkSvGHbE81ph9MI2";
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String firebaseData = fetchDataFromFirestore(db, userId, fechaHoy);
        if (firebaseData == null) {
            System.err.println("No se encontraron datos en Firestore. Programa terminado.");
            return;
        }

        analyzeDataWithOpenAI(firebaseData);
    }

    private static Firestore initializeFirebase() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(PATH_TO_CREDENTIALS)))
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase inicializado correctamente.");
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            System.err.println("Error inicializando Firebase: " + e.getMessage());
            return null;
        }
    }

    private static String fetchDataFromFirestore(Firestore db, String userId, String fechaHoy) {
        DocumentReference docRef = db.collection(FIRESTORE_COLLECTION)
                .document(userId)
                .collection(FIRESTORE_SUBCOLLECTION)
                .document(fechaHoy);

        try {
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot documentSnapshot = future.get();

            if (documentSnapshot.exists()) {
                Double luminosidadPromedio = documentSnapshot.getDouble("luminosidadPromedio");
                Double ruidoPromedio = documentSnapshot.getDouble("ruidoPromedio");
                Double presion1Promedio = documentSnapshot.getDouble("presion1Promedio");
                Double presion2Promedio = documentSnapshot.getDouble("presion2Promedio");
                Double temperaturaPromedio = documentSnapshot.getDouble("temperaturaPromedio");
                Double humedadPromedio = documentSnapshot.getDouble("humedadPromedio");
                Double proximidadPromedio = documentSnapshot.getDouble("proximidadPromedio");

                return "{"
                        + "\"luminosidadPromedio\": " + (luminosidadPromedio != null ? luminosidadPromedio : 0) + ","
                        + "\"ruidoPromedio\": " + (ruidoPromedio != null ? ruidoPromedio : 0) + ","
                        + "\"temperaturaPromedio\": " + (temperaturaPromedio != null ? temperaturaPromedio : 0) + ","
                        + "\"humedadPromedio\": " + (humedadPromedio != null ? humedadPromedio : 0) + ","
                        + "}";


            } else {
                System.err.println("El documento no existe.");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los datos de Firestore: " + e.getMessage());
            return null;
        }

    }

    private static void analyzeDataWithOpenAI(String firebaseData) {
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(ENDPOINT)
                .credential(new AzureKeyCredential(API_KEY))
                .buildClient();
        System.out.println(firebaseData);
        String fixedPrompt = "Analiza los datos de una oficina: " + firebaseData;

        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage("Eres un asistente de análisis de datos."));
        chatMessages.add(new ChatRequestUserMessage(fixedPrompt));

        ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
        ChatCompletions chatCompletions = client.getChatCompletions(MODEL, options);

        ChatChoice choice = chatCompletions.getChoices().get(0);
        String assistantResponse = choice.getMessage().getContent();

        System.out.println("Datos enviados: " + fixedPrompt);
        System.out.println("Asistente: " + assistantResponse);

        // Ruta relativa para crear el archivo en el mismo directorio que el .jar
        String filePath = "fichero.txt";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Datos enviados: " + fixedPrompt + "\n\n");
            writer.write("Respuesta del asistente: " + assistantResponse);
            System.out.println("La respuesta se ha guardado en 'fichero.txt'");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

}
