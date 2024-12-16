#include <M5Stack.h>
#include "DHT.h"
#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>
#include "sedora1_map.h"
#include <PubSubClient.h>

// Definiciones
#define DHTPIN 26
#define DHTTYPE DHT11
#define MIC_PIN 35
#define LDR_PIN 36
#define WIFI_SSID "MOVISTAR EK639"
#define WIFI_PASSWORD "bloodborne"
#define MQTT_SERVER "broker.hivemq.com"
#define MQTT_PORT 1883
#define MQTT_CLIENT_NAME "Sedora1"

#define BLANCO 0xFFFF
#define NEGRO 0x0000
#define ROJO 0xF800
#define VERDE 0x07E0
#define AZUL 0x001F

DHT dht(DHTPIN, DHTTYPE);
WiFiClient espClient;
PubSubClient mqttClient(espClient);
AsyncUDP udp;

int thresholdHigh = 800;
int thresholdLow = 200;
int prevSoundState = -1;
int prevLightState = -1;

float temperatura = 0.0;
float humedad = 0.0;
int micValue = 0;
int ldrValue = 0;
int distancia = 0;
float pesoAsiento = 0.0;
float pesoRespaldo = 0.0;

char texto[200];
boolean recibidoDist = false;
boolean recibidoPes = false;

int currentScreen = 0;
unsigned long lastPublishTime = 0;

// Mutex para acceso a la pantalla LCD
portMUX_TYPE lcdMutex = portMUX_INITIALIZER_UNLOCKED;

//------------------------------------------------
// Funciones para conexión WiFi y MQTT
//------------------------------------------------
void conectarWiFi() {
  M5.Lcd.setTextSize(2);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    M5.Lcd.fillScreen(NEGRO);
    M5.Lcd.setTextColor(ROJO);
    M5.Lcd.println("[Error] No se pudo conectar a WiFi.");
    while (true) delay(1000);
  }
  M5.Lcd.fillScreen(NEGRO);
  M5.Lcd.setTextColor(VERDE);
  M5.Lcd.println("[OK] Conexión WiFi establecida.");
}

void conectarMQTT() {
  while (!mqttClient.connected()) {
    if (mqttClient.connect(MQTT_CLIENT_NAME)) {
      Serial.println("MQTT conectado.");
    } else {
      Serial.println("Error al conectar MQTT, reintentando...");
      delay(5000);
    }
  }
}

//------------------------------------------------
// Funciones UDP
//------------------------------------------------
void iniciarUDP() {
  if (udp.listen(1234)) {
    udp.onPacket([](AsyncUDPPacket packet) {
      strncpy(texto, (char*)packet.data(), sizeof(texto));
      texto[sizeof(texto) - 1] = '\0';

      JsonDocument doc;
      DeserializationError error = deserializeJson(doc, texto);

      if (!error) {
        if (doc["Distancia"] != NULL) {
          distancia = doc["Distancia"];
          recibidoDist = true;
        } else if (doc["PesoAsiento"] != NULL && doc["PesoRespaldo"] != NULL) {
          pesoAsiento = doc["PesoAsiento"];
          pesoRespaldo = doc["PesoRespaldo"];
          recibidoPes = true;
        }
      }
    });
  }
}

//------------------------------------------------
// Funciones de visualización
//------------------------------------------------
void mostrarHumedad(int x, int y, float h) {
  M5.Lcd.setCursor(x, y);
  M5.Lcd.printf("Humedad: %.1f%%", h);
}

void mostrarTemperatura(int x, int y, float t) {
  M5.Lcd.setCursor(x, y);
  M5.Lcd.printf("Temperatura: %.1f°C", t);
}

void mostrarNivelDeSonido(int x, int y, int micValue) {
  int currentState = (micValue > thresholdHigh) ? 1 : ((micValue < thresholdLow) ? 0 : prevSoundState);
  M5.Lcd.setCursor(x, y);
  M5.Lcd.printf("Sonido: %s", (currentState == 1) ? "Inadecuado" : "Adecuado");
  prevSoundState = currentState;
}

void mostrarLuminosidad(int x, int y, int ldrValue) {
  M5.Lcd.setCursor(x, y);
  M5.Lcd.printf("Luminosidad: %s", (ldrValue < 800) ? "Adecuada" : "Inadecuada");
}

void mostrarDatosUDP_PESO() {
  if (recibidoPes) {
    recibidoPes = false;
    M5.Lcd.setCursor(10, 170);
    M5.Lcd.printf("Peso Asiento: %.2f kg", pesoAsiento);

    M5.Lcd.setCursor(10, 190);
    M5.Lcd.printf("Peso Respaldo: %.2f kg", pesoRespaldo);
  }
}

void verPantallaPrincipal() {
  portENTER_CRITICAL(&lcdMutex);
  M5.Lcd.fillScreen(BLANCO);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("Pantalla Principal");

  mostrarNivelDeSonido(10, 50, micValue);
  mostrarLuminosidad(10, 70, ldrValue);
  mostrarHumedad(10, 90, humedad);
  mostrarTemperatura(10, 110, temperatura);
  M5.Lcd.setCursor(10, 130);
  M5.Lcd.printf("Distancia: %d cm", distancia);
  mostrarDatosUDP_PESO();  // Mostrar el peso del asiento y respaldo
  portEXIT_CRITICAL(&lcdMutex);
}

void verPantallaTemperatura() {
  portENTER_CRITICAL(&lcdMutex);
  M5.Lcd.fillScreen(BLANCO);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("Pantalla Temperatura");
  mostrarTemperatura(10, 50, temperatura);
  portEXIT_CRITICAL(&lcdMutex);
}

void verPantallaHumedad() {
  portENTER_CRITICAL(&lcdMutex);
  M5.Lcd.fillScreen(BLANCO);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("Pantalla Humedad");
  mostrarHumedad(10, 50, humedad);
  portEXIT_CRITICAL(&lcdMutex);
}

void verPantallaSonido() {
  portENTER_CRITICAL(&lcdMutex);
  M5.Lcd.fillScreen(BLANCO);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("Pantalla Sonido");
  mostrarNivelDeSonido(10, 50, micValue);
  portEXIT_CRITICAL(&lcdMutex);
}

void verPantallaLuminosidad() {
  portENTER_CRITICAL(&lcdMutex);
  M5.Lcd.fillScreen(BLANCO);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("Pantalla Luminosidad");
  mostrarLuminosidad(10, 50, ldrValue);
  portEXIT_CRITICAL(&lcdMutex);
}

void verPantallaDistancia() {
  portENTER_CRITICAL(&lcdMutex);
  M5.Lcd.fillScreen(BLANCO);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("Pantalla Distancia");
  M5.Lcd.setCursor(10, 50);
  M5.Lcd.printf("Distancia: %d cm", distancia);
  portEXIT_CRITICAL(&lcdMutex);
}

void verPantallaPeso() {
  portENTER_CRITICAL(&lcdMutex);
  M5.Lcd.fillScreen(BLANCO);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("Pantalla Peso");
  mostrarDatosUDP_PESO();
  portEXIT_CRITICAL(&lcdMutex);
}

//------------------------------------------------
// Funciones de tareas
//------------------------------------------------
void tareaSensores(void* parameter) {
  for (;;) {
    temperatura = dht.readTemperature();
    humedad = dht.readHumidity();
    micValue = analogRead(MIC_PIN);
    ldrValue = analogRead(LDR_PIN);
    delay(2000);
  }
}

void tareaMQTT(void* parameter) {
  for (;;) {
    if (millis() - lastPublishTime >= 5000) {
      lastPublishTime = millis();

      if (!mqttClient.connected()) {
        conectarMQTT();
      }

      mqttClient.loop();
      char buffer[50];
      snprintf(buffer, sizeof(buffer), "%.1f", temperatura);
      mqttClient.publish("Sedora/sensores/temperatura", buffer);

      snprintf(buffer, sizeof(buffer), "%.1f", humedad);
      mqttClient.publish("Sedora/sensores/humedad", buffer);

      mqttClient.publish("Sedora/sensores/sonido", (micValue > thresholdHigh) ? "Inadecuado" : "Adecuado");
      mqttClient.publish("Sedora/sensores/luz", (ldrValue < 800) ? "Adecuada" : "Inadecuada");

      snprintf(buffer, sizeof(buffer), "%d", distancia);
      mqttClient.publish("Sedora/sensores/distancia", buffer);

      snprintf(buffer, sizeof(buffer), "%.1f", pesoAsiento);
      mqttClient.publish("Sedora/sensores/pesoAsiento", buffer);

      snprintf(buffer, sizeof(buffer), "%.1f", pesoRespaldo);
      mqttClient.publish("Sedora/sensores/pesoRespaldo", buffer);
    }
    delay(500);
  }
}

//------------------------------------------------
// Setup y Loop principal
//------------------------------------------------
void setup() {
  M5.begin();
  M5.Lcd.drawBitmap(0, 0, SEDORA1_COPY_SMALL_WIDTH, SEDORA1_COPY_SMALL_HEIGHT, sedora1_copy_Small);
  delay(3000);

  conectarWiFi();
  mqttClient.setServer(MQTT_SERVER, MQTT_PORT);
  iniciarUDP();
  dht.begin();

  xTaskCreate(tareaSensores, "Tarea Sensores", 4096, NULL, 1, NULL);
  xTaskCreate(tareaMQTT, "Tarea MQTT", 4096, NULL, 1, NULL);
}

unsigned long lastButtonPress = 0;
const unsigned long debounceDelay = 200; // 200 ms de debounce

void loop() {
 M5.update();  // Actualiza el estado de los botones
  
  // Avanzar pantalla con el botón B
  if (M5.BtnB.wasPressed() && (millis() - lastButtonPress) > debounceDelay) {
    currentScreen = (currentScreen + 1) % 7;  // Ciclo entre 7 pantallas
    lastButtonPress = millis();  // Registra el tiempo de la última presión
  }

  // Retroceder pantalla con el botón C
  if (M5.BtnC.wasPressed() && (millis() - lastButtonPress) > debounceDelay) {
    currentScreen = (currentScreen - 1 + 7) % 7;  // Maneja retroceso cíclico
    lastButtonPress = millis();  // Registra el tiempo de la última presión
  }

  if (M5.BtnB.wasPressed()) {
  Serial.println("Botón B presionado");
}
if (M5.BtnC.wasPressed()) {
  Serial.println("Botón C presionado");
}


  // Mostrar la pantalla correspondiente
  switch (currentScreen) {
    case 0:
      verPantallaPrincipal();
      break;
    case 1:
      verPantallaTemperatura();
      break;
    case 2:
      verPantallaHumedad();
      break;
    case 3:
      verPantallaSonido();
      break;
    case 4:
      verPantallaLuminosidad();
      break;
    case 5:
      verPantallaDistancia();
      break;
    case 6:
      verPantallaPeso();
      break;
    default:
      verPantallaPrincipal();
      break;
  }

  delay(100);  // Agregar un pequeño retraso para evitar problemas de rendimiento
}
