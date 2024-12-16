#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>

// Configuración WiFi
const char* ssid = "MiFibra-3078";
const char* password = "V5AQboPQ";

// Pines del sensor HC-SR04
const int trigPin = 26;
const int echoPin = 25;

// Pin del LED
const int ledPin = 13;

// Objetos globales
AsyncUDP udp;
StaticJsonDocument<200> jsonBuffer;

// Semáforo para proteger el acceso al sensor
SemaphoreHandle_t mutexSensor;

// Cola para comunicación entre tareas
QueueHandle_t colaDistancia;

// Handle para tareas FreeRTOS
TaskHandle_t taskEnviarDatos;
TaskHandle_t taskControlLED;

// Funciones para WiFi
void setupWiFi() {
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        Serial.println("Conectando a WiFi...");
        vTaskDelay(1000 / portTICK_PERIOD_MS);
    }

    Serial.print("Conectado a la IP: ");
    Serial.println(WiFi.localIP());
}

// Función para medir la distancia
long medirDistancia() {
    long duration, distance;
    digitalWrite(trigPin, LOW);
    ets_delay_us(2); 
    digitalWrite(trigPin, HIGH);
    ets_delay_us(10); 
    digitalWrite(trigPin, LOW);

    duration = pulseIn(echoPin, HIGH);
    distance = duration * 10 / 292 / 2;
    return distance;
}

// Tarea para enviar los datos por UDP
void tareaEnviarDatos(void* parameter) {
    for (;;) {
        long distance;
        xSemaphoreTake(mutexSensor, portMAX_DELAY); 
        distance = medirDistancia();
        xSemaphoreGive(mutexSensor); 

        jsonBuffer["Distancia"] = distance;
        jsonBuffer["Alerta"] = (distance < 8);

        char textoEnvio[200];
        serializeJson(jsonBuffer, textoEnvio);
        udp.broadcastTo(textoEnvio, 1234);

        // Enviar distancia a la cola
        xQueueSend(colaDistancia, &distance, portMAX_DELAY);

        vTaskDelay(1000 / portTICK_PERIOD_MS); 
    }
}

// Tarea para controlar el LED
void tareaControlLED(void* parameter) {
    for (;;) {
        long distance;

        // Recibir distancia desde la cola
        if (xQueueReceive(colaDistancia, &distance, portMAX_DELAY)) {
            if (distance < 8) {
                digitalWrite(ledPin, HIGH);
            } else {
                digitalWrite(ledPin, LOW);
            }
        }
        vTaskDelay(50 / portTICK_PERIOD_MS); // Verificar cada 50 ms
    }
}

void setup() {
    Serial.begin(115200);

    // Configuración de pines
    pinMode(trigPin, OUTPUT);
    pinMode(echoPin, INPUT);
    pinMode(ledPin, OUTPUT);

    // Conexión WiFi
    setupWiFi();

    // Inicializar mutex
    mutexSensor = xSemaphoreCreateMutex();

    // Inicializar cola
    colaDistancia = xQueueCreate(5, sizeof(long));

    // Inicializar tareas
    xTaskCreatePinnedToCore(tareaEnviarDatos, "Enviar Datos", 4096, NULL, 1, &taskEnviarDatos, 1);
    xTaskCreatePinnedToCore(tareaControlLED, "Controlar LED", 1024, NULL, 1, &taskControlLED, 1);
}

void loop() {
    
    vTaskDelay(1000 / portTICK_PERIOD_MS); 
}
