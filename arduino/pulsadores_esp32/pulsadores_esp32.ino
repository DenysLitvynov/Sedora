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

#define SLEEP_TIME 5000000UL   
#define MEASURE_INTERVAL 3000  

unsigned long lastMeasureTime = 0;

AsyncUDP udp;
StaticJsonDocument<200> jsonBuffer;

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

bool leerPulsador(int pin) {
  return digitalRead(pin) == LOW ? 1 : 0; 
}

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

void setup() {
  Serial.begin(115200);

  pinMode(PULSADOR_1_PIN, INPUT_PULLUP);
  pinMode(PULSADOR_2_PIN, INPUT_PULLUP);

  setupWiFi();
}

void loop() {
  int asiento = leerPulsador(PULSADOR_1_PIN);
  int respaldo = leerPulsador(PULSADOR_2_PIN);

  Serial.printf("Asiento: %d\n", asiento); 
  Serial.printf("Respaldo: %d\n", respaldo); 
  enviarDatos(asiento, respaldo);

  if (asiento || respaldo) {
    lastMeasureTime = millis();  
  }

  if (millis() - lastMeasureTime >= MEASURE_INTERVAL) {
    Serial.println("Entrando en deep sleep...");

    esp_sleep_enable_timer_wakeup(SLEEP_TIME);
    esp_deep_sleep_start(); 
  }

  delay(1000);
}
