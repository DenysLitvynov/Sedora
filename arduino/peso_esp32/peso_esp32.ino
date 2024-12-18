#include "HX711.h"
#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>
#include <esp_sleep.h>

// Configuración WiFi
const char* ssid = "MiFibra-3078";
const char* password = "V5AQboPQ";

// Pines para el sensor del asiento
#define DT_PIN_ASIENTO 26
#define SCK_PIN_ASIENTO 25

// Pines para el sensor del respaldo
#define DT_PIN_RESPALDO 19
#define SCK_PIN_RESPALDO 18

HX711 scaleAsiento;
HX711 scaleRespaldo;

AsyncUDP udp;
StaticJsonDocument<200> jsonBuffer;

#define SLEEP_TIME 50000000UL

// Función para configurar el WiFi
void setupWiFi() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  Serial.println("Conectando a WiFi...");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nWiFi conectado");
  Serial.print("Dirección IP: ");
  Serial.println(WiFi.localIP());
}

float medirPeso(HX711& scale) {
  if (!scale.is_ready()) {
    Serial.println("Sensor no está listo");
    return 0.0;
  }

  float peso = scale.get_units(10); 
  return peso;
}

// Función para enviar los datos por UDP
void enviarDatos(float pesoAsiento, float pesoRespaldo) {
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
  serializeJson(jsonBuffer, textoEnvio);

  udp.broadcastTo(textoEnvio, 1234);

  Serial.println("Datos enviados por UDP:");
  Serial.println(textoEnvio);
}

void setup() {
  Serial.begin(115200);

  scaleAsiento.begin(DT_PIN_ASIENTO, SCK_PIN_ASIENTO);
  scaleRespaldo.begin(DT_PIN_RESPALDO, SCK_PIN_RESPALDO);

  scaleAsiento.set_scale(2280.f);  
  scaleAsiento.tare();             
  scaleRespaldo.set_scale(2280.f); 
  scaleRespaldo.tare();          

  setupWiFi();
}

void loop() {
  float pesoAsiento = medirPeso(scaleAsiento);
  float pesoRespaldo = medirPeso(scaleRespaldo);

  Serial.printf("Peso Asiento: %.2f g\n", pesoAsiento);
  Serial.printf("Peso Respaldo: %.2f g\n", pesoRespaldo);

  enviarDatos(pesoAsiento, pesoRespaldo);

  Serial.println("Entrando en deep sleep...");
  delay(100); 

  WiFi.disconnect(true);
  WiFi.mode(WIFI_OFF);
  esp_sleep_enable_timer_wakeup(SLEEP_TIME);
  esp_deep_sleep_start();
}
