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
DHT dht(DHTPIN, DHTTYPE);
int micPin = 35;
int ldrPin = 36;

int thresholdHigh = 800;
int thresholdLow = 200;
int prevSoundState = -1;
int prevLightState = -1;
int distancia = 0;
float pesoAsiento = 0.0;
float pesoRespaldo = 0.0;

#define BLANCO 0xFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x009774
#define AZUL 0x001F

//Cambiar a la red wifi que se vaya a usar cada vez
const char* ssid = "MiFibra-3078";
const char* password = "V5AQboPQ";

char texto[200];
boolean recibidoDist = false;
boolean recibidoPes = false;

AsyncUDP udp;
int currentScreen = 0;

WiFiClient espClient;

PubSubClient client(espClient);
const char* mqtt_server = "test.mosquitto.org";
const int mqtt_port = 1883;
const char* mqtt_client_name = "Sedora1";
unsigned long lastPublishTime = 0;

//------------------------------------------------
// Funciones que tienen que ver con la conexion por WIFI UDP y MQTT
//------------------------------------------------

//------------------------------------------------
//------------------------------------------------
void conectarWiFi() {
  M5.Lcd.setTextSize(2);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    M5.Lcd.fillScreen(NEGRO);
    M5.Lcd.setTextColor(ROJO);
    M5.Lcd.println("[Servidor] Error al conectar a WiFi!");
    while (true) delay(1000);
  }

  M5.Lcd.fillScreen(NEGRO);
  M5.Lcd.setTextColor(VERDE);
  M5.Lcd.println("[Servidor] Conexion OK.");
}
//------------------------------------------------
//------------------------------------------------
void conectarMQTT() {
  while (!client.connected()) {
    M5.Lcd.setTextColor(VERDE);
    Serial.print("Conectando a MQTT...");

    if (client.connect(mqtt_client_name)) {
      Serial.print("MQTT conectado.");
    } else {
      Serial.print("Error MQTT, reintentando...");
      delay(5000);
    }
  }
}
//------------------------------------------------
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
          recibidoDist = true;
        } else if (doc["PesoAsiento"] != NULL && doc["PesoRespaldo"] != NULL) {
          recibidoPes = true;
        }
      }
    });
  }
}
//------------------------------------------------
//------------------------------------------------
void mostrarDatosUDP_DISTANCIA() {
  if (recibidoDist) {
    recibidoDist = false;
    DynamicJsonDocument jsonBufferRecv(200);
    DeserializationError error = deserializeJson(jsonBufferRecv, texto);
    if (error) return;

    distancia = jsonBufferRecv["Distancia"];
    bool alerta = jsonBufferRecv["Alerta"];

    M5.Lcd.setCursor(10, 150);
    M5.Lcd.print("Distancia: ");
    M5.Lcd.printf("%d cm", distancia);

    M5.Lcd.setCursor(10, 130);
    if (alerta) {
      M5.Lcd.println("¡ALERTA!");
      M5.Speaker.tone(500, 200);
      delay(500);
      M5.Speaker.mute();
    } else {
      M5.Lcd.println("Distancia segura.");
      M5.Speaker.mute();
    }
  }
}
//------------------------------------------------
//------------------------------------------------
void mostrarDatosUDP_PESO() {
if (recibidoPes) {
    recibidoPes = false;
    DynamicJsonDocument jsonBufferRecv(200);
    DeserializationError error = deserializeJson(jsonBufferRecv, texto);
    if (error) return;

    pesoAsiento = jsonBufferRecv["PesoAsiento"];
    pesoRespaldo = jsonBufferRecv["PesoRespaldo"];

    M5.Lcd.setCursor(10, 170);
    M5.Lcd.print("Peso Asiento: ");
    M5.Lcd.printf("%.2f kg", pesoAsiento);

    M5.Lcd.setCursor(10, 190);
    M5.Lcd.print("Peso Respaldo: ");
    M5.Lcd.printf("%.2f kg", pesoRespaldo);
  }
}
//------------------------------------------------
//Funciones que tiene que ver con los sensores
//------------------------------------------------
void mostrarHumedad(int x, int y, float h) {
  M5.Lcd.setCursor(x, y);
  M5.Lcd.print("Humedad: ");
  M5.Lcd.print(h);
  M5.Lcd.print("%");
}
//------------------------------------------------
//------------------------------------------------
void mostrarTemperatura(int x, int y, float t) {
  M5.Lcd.setCursor(x, y);
  M5.Lcd.print("Temperatura: ");
  M5.Lcd.print(t);
  M5.Lcd.print("°C");
}
//------------------------------------------------
//------------------------------------------------
void mostrarNivelDeSonido(int x, int y, int micValue) {
  int currentState;

  if (micValue > thresholdHigh) {
    currentState = 1;
  } else if (micValue < thresholdLow) {
    currentState = 0;
  } else {
    currentState = prevSoundState;
  }

  M5.Lcd.setCursor(x, y);
  if (currentState == 1) {
    M5.Lcd.println("Sonido: Inadecuado");
  } else {
    M5.Lcd.println("Sonido: Adecuado");
  }
  prevSoundState = currentState;
}
//------------------------------------------------
//------------------------------------------------
void mostrarLuminosidad(int x, int y, int ldrValue) {
  M5.Lcd.setCursor(x, y);
  if (ldrValue < 800) {
    M5.Lcd.println("Luminosidad: Adecuada");
  } else {
    M5.Lcd.println("Luminosidad: Inadecuada");
  }
}
//------------------------------------------------
//Funiocnes para las pantallas del M5
//------------------------------------------------
void verPantallaPrincipal(float hum, float temp, int micValue, int ldrValue) {
  M5.Lcd.clear(BLANCO);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.setTextSize(2);
  M5.Lcd.println("Pantalla Principal");

  mostrarHumedad(10, 110, hum);
  mostrarTemperatura(10, 90, temp);
  mostrarNivelDeSonido(10, 50, micValue);
  mostrarLuminosidad(10, 70, ldrValue);
  mostrarDatosUDP_DISTANCIA();
  mostrarDatosUDP_PESO();
}
//------------------------------------------------
//------------------------------------------------
void verPantallaTemp(float temp) {
  M5.Lcd.clear(AZUL);
  M5.Lcd.setTextColor(BLANCO);
  M5.Lcd.setTextSize(2);

  M5.Lcd.setCursor(10, 20);
  M5.Lcd.println("Temperatura");

  mostrarTemperatura(10, 70, temp);
}
//------------------------------------------------
//------------------------------------------------
void verPantallaHum(float hum) {
  M5.Lcd.clear(AZUL);
  M5.Lcd.setTextColor(BLANCO);
  M5.Lcd.setTextSize(2);

  M5.Lcd.setCursor(10, 20);
  M5.Lcd.println("Humedad");

  mostrarHumedad(10, 70, hum);
}
//------------------------------------------------
//------------------------------------------------
void verPantallaSon(int micValue) {
  M5.Lcd.clear(ROJO);
  M5.Lcd.setTextColor(BLANCO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 20);
  M5.Lcd.println("Nivel de Sonido");

  mostrarNivelDeSonido(10, 70, micValue);
}
//------------------------------------------------
//------------------------------------------------
void verPantallaLuz(int ldrValue) {
  M5.Lcd.clear(VERDE);
  M5.Lcd.setTextColor(NEGRO);
  M5.Lcd.setTextSize(2);

  M5.Lcd.setCursor(10, 20);
  M5.Lcd.println("Luminosidad");

  mostrarLuminosidad(10, 70, ldrValue);
}
//------------------------------------------------
//------------------------------------------------
void verPantallaDist() {
  M5.Lcd.clear(NEGRO);
  M5.Lcd.setTextColor(BLANCO);
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 20);
  M5.Lcd.println("Distancia");
  mostrarDatosUDP_DISTANCIA();
}
//------------------------------------------------
//Funcion para publicar los datos al Boker MQTT
//------------------------------------------------
void publicarDatos(float temp, float hum, int micValue, int ldrValue, int distancia, float pesoAsiento, float pesoRespaldo) {
  if (!client.connected()) {
    conectarMQTT();
  }

  char tempStr[10];
  dtostrf(temp, 6, 2, tempStr);
  client.publish("Sedora/sensores/temperatura", tempStr);

  char humStr[10];
  dtostrf(hum, 6, 2, humStr);
  client.publish("Sedora/sensores/humedad", humStr);

  // Publicar sonido: Adecuado/Inadecuado
  String soundMessage = (micValue > thresholdHigh) ? "Inadecuado" : "Adecuado";
  client.publish("Sedora/sensores/sonido", soundMessage.c_str());

  // Publicar luz: Adecuada/Inadecuada
  String lightMessage = (ldrValue < 800) ? "Adecuada" : "Inadecuada";
  client.publish("Sedora/sensores/luz", lightMessage.c_str());

  char distStr[10];
  itoa(distancia, distStr, 10);
  client.publish("Sedora/sensores/distancia", distStr);

  char pesoAsientoStr[10], pesoRespaldoStr[10];
  dtostrf(pesoAsiento, 6, 2, pesoAsientoStr);
  dtostrf(pesoRespaldo, 6, 2, pesoRespaldoStr);

  client.publish("Sedora/sensores/pesoAsiento", pesoAsientoStr);
  client.publish("Sedora/sensores/pesoRespaldo", pesoRespaldoStr);

  // Publicar Postura: correcto/Incorrecto
  String posicionMessage;
  if (pesoAsiento >= 9 && pesoRespaldo >= 9) {
    posicionMessage = "Correcto";
  } else {
    posicionMessage = "Incorrecto";
  }
  client.publish("Sedora/sensores/posicion", posicionMessage.c_str());
}
//------------------------------------------------
//------------------------------------------------
void setup() {
  M5.begin();
  M5.Lcd.drawBitmap(0, 0, SEDORA1_COPY_SMALL_WIDTH, SEDORA1_COPY_SMALL_HEIGHT, sedora1_copy_Small);
  delay(3000);
  conectarWiFi();
  client.setServer(mqtt_server, mqtt_port);
  iniciarUDP();
  dht.begin();
  verPantallaPrincipal(0, 0, 0, 0);
}
//------------------------------------------------
//------------------------------------------------
void loop() {
  M5.update();
  client.loop();
  unsigned long currentMillis = millis();

  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  int micValue = analogRead(micPin);
  int ldrValue = analogRead(ldrPin);

  if (currentMillis - lastPublishTime >= 5000) {
    lastPublishTime = currentMillis;
    publicarDatos(temp, hum, micValue, ldrValue, distancia, pesoAsiento, pesoRespaldo);
  }

  if (M5.BtnB.wasPressed()) {
    currentScreen++;
    if (currentScreen > 5) {
      currentScreen = 0;
    }
  }

  if (M5.BtnC.wasPressed()) {
    currentScreen--;
    if (currentScreen < 0) {
      currentScreen = 5;
    }
  }

  switch (currentScreen) {
    case 0:
      verPantallaPrincipal(hum, temp, micValue, ldrValue);
      break;
    case 1:
      verPantallaTemp(temp);
      break;
    case 2:
      verPantallaSon(micValue);
      break;
    case 3:
      verPantallaLuz(ldrValue);
      break;
    case 4:
      verPantallaDist();
      break;
    case 5:
      verPantallaHum(hum);
      break;
  }
  delay(2000);
}
