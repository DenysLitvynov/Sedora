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

unsigned long lastActiveTime = 0; 
unsigned long lastSendTime = 0;  

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
  lastActiveTime = millis(); 
  lastSendTime = millis();   
}

void loop() {
  unsigned long currentTime = millis();

  // Leer pulsadores
  int asiento = leerPulsador(PULSADOR_1_PIN);
  int respaldo = leerPulsador(PULSADOR_2_PIN);

  if (asiento || respaldo) {
    lastActiveTime = currentTime;
  }

  if (currentTime - lastSendTime >= MEASURE_INTERVAL) {
    enviarDatos(asiento, respaldo);
    lastSendTime = currentTime; 
  }

  if ((asiento == 0 && respaldo == 0) && (currentTime - lastActiveTime >= 10000)) {
    Serial.println("Entrando en deep sleep por 10 segundos...");
    delay(100); 
    esp_sleep_enable_timer_wakeup(SLEEP_TIME);
    esp_deep_sleep_start();
  }

  delay(100); 
}
