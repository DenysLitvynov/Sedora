#include <M5Stack.h>
#include "HX711.h"
#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>

// Configuración WiFi
const char* ssid = "**************";
const char* password = "*********";

#define DT_PIN 26
#define SCK_PIN 25

AsyncUDP udp;
StaticJsonDocument<200> jsonBuffer;

HX711 scale;

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

// Función para medir el peso
long medirPeso() {
  scale.begin(DT_PIN, SCK_PIN);

  scale.set_scale(2280.f);
  scale.tare();
  float peso = scale.get_units(10);

  M5.Lcd.clear();
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.print("Peso: ");
  M5.Lcd.print(peso, 2);
  M5.Lcd.print(" g");

  if (peso > 20) {
    jsonBuffer["Estado"] = "Sentado correctamente";
  } else {
    jsonBuffer["Estado"] = "Sentado incorrectamente";
  }

  enviarDatos(peso);

  delay(500);
  return peso;
}

// Función para enviar los datos por UDP
void enviarDatos(long peso) {
  jsonBuffer["Peso"] = peso;
  char textoEnvio[200];
  serializeJson(jsonBuffer, textoEnvio);
  udp.broadcastTo(textoEnvio, 1234);
}

void setup() {
  Serial.begin(115200);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(ledPin, OUTPUT);

  setupWiFi();
}

void loop() {
  long peso = medirPeso();
  delay(1000);
}
