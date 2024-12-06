#include "HX711.h"
#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>
#include <esp_sleep.h>

// Configuración WiFi
const char* ssid = "MiFibra-3078";  // Nombre de la red WiFi
const char* password = "V5AQboPQ";   // Contraseña de la red WiFi

// Pines para el sensor del asiento
#define DT_PIN_ASIENTO 26 // Pin de datos del sensor del asiento
#define SCK_PIN_ASIENTO 25 // Pin de reloj del sensor del asiento

// Pines para el sensor del respaldo
#define DT_PIN_RESPALDO 19 // Pin de datos del sensor del respaldo
#define SCK_PIN_RESPALDO 18 // Pin de reloj del sensor del respaldo

// Declaración de los objetos para los sensores
HX711 scaleAsiento;
HX711 scaleRespaldo;

// Configuración de comunicación UDP
AsyncUDP udp;
StaticJsonDocument<200> jsonBuffer; // Buffer JSON para preparar los datos a enviar

#define SLEEP_TIME 50000000UL // Tiempo en microsegundos para entrar en deep sleep

// Declaración de colas y semáforo
QueueHandle_t colaDatos; // Cola para almacenar datos de los sensores
SemaphoreHandle_t mutexJson; // Semáforo para proteger el acceso al buffer JSON

// Estructura para los datos de los sensores
typedef struct {
    float pesoAsiento;  // Peso medido por el sensor del asiento
    float pesoRespaldo; // Peso medido por el sensor del respaldo
} DatosSensores;

// Función para configurar el WiFi
void setupWiFi() {
    WiFi.mode(WIFI_STA); // Configurar el modo estación
    WiFi.begin(ssid, password); // Iniciar la conexión WiFi

    Serial.println("Conectando a WiFi...");
    while (WiFi.status() != WL_CONNECTED) { // Esperar hasta que se conecte
        vTaskDelay(500 / portTICK_PERIOD_MS);
        Serial.print(".");
    }

    Serial.println("\nWiFi conectado");
    Serial.print("Dirección IP: ");
    Serial.println(WiFi.localIP()); // Mostrar la dirección IP asignada
}

// Función para medir el peso de un sensor HX711
float medirPeso(HX711& scale) {
    if (!scale.is_ready()) { // Verificar si el sensor está listo
        Serial.println("Sensor no está listo");
        return 0.0;
    }

    float peso = scale.get_units(10); // Medir el peso promediando 10 lecturas
    return peso;
}

// Función para enviar los datos por UDP
void enviarDatosUDP(float pesoAsiento, float pesoRespaldo) {
    if (xSemaphoreTake(mutexJson, portMAX_DELAY) == pdTRUE) { // Tomar el semáforo
        // Preparar los datos en formato JSON
        jsonBuffer["PesoAsiento"] = pesoAsiento;
        jsonBuffer["PesoRespaldo"] = pesoRespaldo;

        String estado;
        if (pesoAsiento > 20 && pesoRespaldo > 20) {
            estado = "Sentado correctamente";
        } else {
            estado = "Sentado incorrectamente";
        }
        jsonBuffer["Estado"] = estado;

        char textoEnvio[200];
        serializeJson(jsonBuffer, textoEnvio); // Serializar el JSON

        udp.broadcastTo(textoEnvio, 1234); // Enviar los datos por UDP

        Serial.println("Datos enviados por UDP:");
        Serial.println(textoEnvio); // Mostrar los datos en el monitor serie

        xSemaphoreGive(mutexJson); // Liberar el semáforo
    }
}

// Tarea para medir el peso del asiento
void tareaMedirAsiento(void* parameter) {
    for (;;) {
        float pesoAsiento = medirPeso(scaleAsiento); // Medir el peso del asiento

        DatosSensores datos;
        datos.pesoAsiento = pesoAsiento;
        datos.pesoRespaldo = 0; // El peso del respaldo se actualiza en otra tarea

        xQueueSend(colaDatos, &datos, portMAX_DELAY); // Enviar datos a la cola
        vTaskDelay(1000 / portTICK_PERIOD_MS); // Esperar 1 segundo
    }
}

// Tarea para medir el peso del respaldo
void tareaMedirRespaldo(void* parameter) {
    for (;;) {
        float pesoRespaldo = medirPeso(scaleRespaldo); // Medir el peso del respaldo

        DatosSensores datos;
        datos.pesoAsiento = 0; // El peso del asiento se actualiza en otra tarea
        datos.pesoRespaldo = pesoRespaldo;

        xQueueSend(colaDatos, &datos, portMAX_DELAY); // Enviar datos a la cola
        vTaskDelay(1000 / portTICK_PERIOD_MS); // Esperar 1 segundo
    }
}

// Tarea para enviar los datos por UDP
void tareaEnviarDatos(void* parameter) {
    DatosSensores datosRecibidos;
    float pesoAsiento = 0, pesoRespaldo = 0;

    for (;;) {
        if (xQueueReceive(colaDatos, &datosRecibidos, portMAX_DELAY) == pdTRUE) { // Recibir datos de la cola
            if (datosRecibidos.pesoAsiento != 0) {
                pesoAsiento = datosRecibidos.pesoAsiento; // Actualizar peso del asiento
            }
            if (datosRecibidos.pesoRespaldo != 0) {
                pesoRespaldo = datosRecibidos.pesoRespaldo; // Actualizar peso del respaldo
            }

            enviarDatosUDP(pesoAsiento, pesoRespaldo); // Enviar datos por UDP
        }
    }
}

// Configuración inicial del programa
void setup() {
    Serial.begin(115200); // Inicializar el puerto serie

    scaleAsiento.begin(DT_PIN_ASIENTO, SCK_PIN_ASIENTO); // Configurar el sensor del asiento
    scaleRespaldo.begin(DT_PIN_RESPALDO, SCK_PIN_RESPALDO); // Configurar el sensor del respaldo

    scaleAsiento.set_scale(2280.f); // Establecer la escala del sensor del asiento
    scaleAsiento.tare(); // Calibrar el sensor del asiento
    scaleRespaldo.set_scale(2280.f); // Establecer la escala del sensor del respaldo
    scaleRespaldo.tare(); // Calibrar el sensor del respaldo

    setupWiFi(); // Configurar la conexión WiFi

    colaDatos = xQueueCreate(10, sizeof(DatosSensores)); // Crear la cola para datos de los sensores
    mutexJson = xSemaphoreCreateMutex(); // Crear el semáforo para el buffer JSON

    // Crear las tareas y asignarlas a los núcleos
    xTaskCreatePinnedToCore(tareaMedirAsiento, "Medir Asiento", 2048, NULL, 1, NULL, 1);
    xTaskCreatePinnedToCore(tareaMedirRespaldo, "Medir Respaldo", 2048, NULL, 1, NULL, 1);
    xTaskCreatePinnedToCore(tareaEnviarDatos, "Enviar Datos", 4096, NULL, 1, NULL, 1);
}

// Bucle principal
void loop() {
    Serial.println("Entrando en deep sleep...");

    WiFi.disconnect(true); // Desconectar WiFi
    WiFi.mode(WIFI_OFF); // Apagar WiFi
    esp_sleep_enable_timer_wakeup(SLEEP_TIME); // Configurar el temporizador para deep sleep
    esp_deep_sleep_start(); // Entrar en modo de sueño profundo
}
