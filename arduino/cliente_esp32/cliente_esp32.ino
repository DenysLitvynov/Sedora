#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>

// Configuración WiFi
const char* ssid = "**************";
const char* password = "*********";

// Pines del sensor HC-SR04
const int trigPin = 26;
const int echoPin = 25;

// Pin del LED
const int ledPin = 13;

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

// Función para medir la distancia
long medirDistancia() {
  long duration, distance;
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  duration = pulseIn(echoPin, HIGH);
  distance = duration * 10 / 292 / 2;
  return distance;
}

// Función para enviar los datos por UDP
void enviarDatos(long distance) {
  jsonBuffer["Distancia"] = distance;
  jsonBuffer["Alerta"] = (distance < 8);
  char textoEnvio[200];
  serializeJson(jsonBuffer, textoEnvio);
  udp.broadcastTo(textoEnvio, 1234);
}

// Función para controlar el LED
void controlarLED(long distance) {
  if (distance < 8) {
    digitalWrite(ledPin, HIGH); 
  } else {
    digitalWrite(ledPin, LOW);  
  }
}

void setup() {
  Serial.begin(115200);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(ledPin, OUTPUT);

  setupWiFi();
}

void loop() {
  long distancia = medirDistancia();
  enviarDatos(distancia);
  controlarLED(distancia); 
  delay(1000);
}
