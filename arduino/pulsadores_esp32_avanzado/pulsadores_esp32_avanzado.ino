#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>
#include <esp_sleep.h>

// Configuración WiFi
const char* ssid = "MiFibra-3078";
const char* password = "V5AQboPQ";

// Pines para los pulsadores
#define PULSADOR_1_PIN 18
#define PULSADOR_2_PIN 19

#define SLEEP_TIME 10000000UL    
#define MEASURE_INTERVAL 5000   
#define INACTIVITY_TIMEOUT 10000  

// Variables globales
unsigned long lastMeasureTime = 0;
unsigned long lastActivityTime = 0;  // Tiempo de la última actividad

AsyncUDP udp;
StaticJsonDocument<200> jsonBuffer;

// Estado de los pulsadores
volatile int asiento = 0;
volatile int respaldo = 0;

// Estado anterior de los pulsadores
volatile int lastAsientoState = HIGH;
volatile int lastRespaldoState = HIGH;

// Función para configurar el WiFi
void setupWiFi() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    Serial.println("Error al conectar a la WiFi");
    while (1) { delay(1000); }
  }

  Serial.print("Conectado a la IP: ");
  Serial.println(WiFi.localIP());
}

// Función para manejar la interrupción del pulsador 1
void ISR_Pulsador1() {
  int currentState = digitalRead(PULSADOR_1_PIN);  
  if (currentState == LOW && lastAsientoState == HIGH) {
    Serial.println("Pulsador 1 presionado");
    lastAsientoState = LOW;
    lastActivityTime = millis();  // Actualizar el tiempo de la última actividad
  } 
  else if (currentState == HIGH && lastAsientoState == LOW) {
    Serial.println("Pulsador 1 liberado");
    lastAsientoState = HIGH;
  }
  asiento = !currentState;  // Actualiza el estado del pulsador
}

// Función para manejar la interrupción del pulsador 2
void ISR_Pulsador2() {
  int currentState = digitalRead(PULSADOR_2_PIN);  
  if (currentState == LOW && lastRespaldoState == HIGH) {
    Serial.println("Pulsador 2 presionado");
    lastRespaldoState = LOW;
    lastActivityTime = millis();  // Actualizar el tiempo de la última actividad
  } 
  else if (currentState == HIGH && lastRespaldoState == LOW) {
    Serial.println("Pulsador 2 liberado");
    lastRespaldoState = HIGH;
  }
  respaldo = !currentState;  // Actualiza el estado del pulsador
}

// Función para enviar datos por UDP
void enviarDatos(int asiento, int respaldo) {
  jsonBuffer["asiento"] = asiento;
  jsonBuffer["respaldo"] = respaldo;

  String estado;
  if (asiento == 1 && respaldo == 1) {
    estado = "Sentado correctamente";
  } else if (asiento == 1) {
    estado = "Te falta el otro (Pulsador 2)";
  } else if (respaldo == 1) {
    estado = "Te falta el otro (Pulsador 1)";
  } else {
    estado = "Ninguno pulsado";
  }
  jsonBuffer["Estado"] = estado;

  char textoEnvio[200];
  serializeJson(jsonBuffer, textoEnvio);

  udp.broadcastTo(textoEnvio, 1234);

  Serial.println("Datos enviados por UDP:");
  Serial.println(textoEnvio);
}

// Tarea para enviar datos cada 5 segundos
void tareaMandarDatos(void *pvParameters) {
  for (;;) {
    if (millis() - lastMeasureTime >= MEASURE_INTERVAL) {
      enviarDatos(asiento, respaldo);  // Enviar los datos por UDP
      lastMeasureTime = millis();      // Actualizar el tiempo de la última medición
    }
    vTaskDelay(1000 / portTICK_PERIOD_MS);  // Evitar que la tarea consuma demasiada CPU
  }
}

// Tarea para verificar inactividad
void tareaVerificarInactividad(void *pvParameters) {
  for (;;) {
    // Verificar si los pulsadores están presionados y reiniciar el temporizador de inactividad
    if (digitalRead(PULSADOR_1_PIN) == LOW || digitalRead(PULSADOR_2_PIN) == LOW) {
      lastActivityTime = millis();  
    }

    
    if (millis() - lastActivityTime >= INACTIVITY_TIMEOUT) {
      Serial.println("No hubo actividad por 10 segundos, entrando en deep sleep...");
      esp_sleep_enable_timer_wakeup(SLEEP_TIME);  
      esp_deep_sleep_start(); 
    }
    vTaskDelay(5000 / portTICK_PERIOD_MS);  
  }
}


void setup() {
  Serial.begin(115200);

  pinMode(PULSADOR_1_PIN, INPUT_PULLUP);
  pinMode(PULSADOR_2_PIN, INPUT_PULLUP);

  // Configurar interrupciones
  attachInterrupt(digitalPinToInterrupt(PULSADOR_1_PIN), ISR_Pulsador1, CHANGE);
  attachInterrupt(digitalPinToInterrupt(PULSADOR_2_PIN), ISR_Pulsador2, CHANGE);

  setupWiFi();

  // Crear tareas
  xTaskCreatePinnedToCore(tareaMandarDatos, "SendData", 2048, NULL, 1, NULL, 1); 
  xTaskCreatePinnedToCore(tareaVerificarInactividad, "CheckInactivity", 2048, NULL, 1, NULL, 0); 
}

void loop() {
  
  delay(1000); 
}
