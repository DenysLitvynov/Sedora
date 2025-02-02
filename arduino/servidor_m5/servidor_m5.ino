#include <M5Stack.h>
#include "DHT.h"
#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>
#include "sedora1_map.h"
#include <PubSubClient.h>
#include <WiFiClientSecure.h> // Necesario para WebSocket

// Definiciones
#define DHTPIN 26
#define DHTTYPE DHT11
#define LED_PIN 2 // Pin donde está conectado el LED
DHT dht(DHTPIN, DHTTYPE);
int micPin = 35;
int ldrPin = 36;

int thresholdHigh = 800;
int thresholdLow = 200;
int prevSoundState = -1;
int prevLightState = -1;
int distancia = 0;
int pulsador1 = 0;
int pulsador2 = 0;

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
boolean recibidoPulsadores = false;

AsyncUDP udp;
int currentScreen = 0;

//=============LED=============
#define MQTT_CLIENT_ID "M5Stack_LED"
#define MQTT_TOPIC_LED "Sedora/led/status"
const char* mqtt_server_led = "broker.hivemq.com";
const char* mqtt_client_led_name = "M5Stack_LED";
const int mqtt_port_led = 1883;
WiFiClient espClient_led;    // En lugar de WiFiClientSecure
PubSubClient client_led(espClient_led);
// Variables
bool ledState = false;
//=============LED=============

WiFiClient espClient;

PubSubClient client(espClient);
const char* mqtt_server = "broker.hivemq.com";
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
// Función para conectar MQTT del LED
void conectarMQTTLed() {
  while (!client_led.connected()) {
    if (client_led.connect(mqtt_client_led_name)) {
      Serial.println("Conectado al MQTT LED!");
      client_led.subscribe(MQTT_TOPIC_LED);
    } else {
      Serial.println("Error MQTT LED, reintentando...");
      delay(5000);
    }
  }
}
//------------------------------------------------
// Callback de mensajes MQTT (LED)
//------------------------------------------------
void callbackMQTTLed(char* topic, byte* payload, unsigned int length) {
  String message = "";
  for (unsigned int i = 0; i < length; i++) {
    message += (char)payload[i];
  }

  if (String(topic) == MQTT_TOPIC_LED) {
    if (message.equalsIgnoreCase("ON")) {
      digitalWrite(LED_PIN, HIGH);
      Serial.println("LED ENCENDIDO.");
    } else if (message.equalsIgnoreCase("OFF")) {
      digitalWrite(LED_PIN, LOW);
      Serial.println("LED APAGADO.");
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


      DynamicJsonDocument doc(200);
      DeserializationError error = deserializeJson(doc, texto);

      if (!error) {
        if (doc.containsKey("Distancia")) {
          recibidoDist = true;
          Serial.println("Datos de distancia recibidos");
        } else if (doc.containsKey("asiento") && doc.containsKey("respaldo")) {
          recibidoPulsadores = true;
          Serial.println("Datos de pulsadores recibidos");
        }
      } else {
        Serial.println("Error al deserializar JSON");
        Serial.println(error.c_str());  
      }
    });
  } else {
    Serial.println("Error al iniciar UDP");
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
void mostrarDatosUDP_PULSADORES() {
  const char* estado = "Sentado incorrectamente"; // Mantén el estado como local

  if (recibidoPulsadores) {
    recibidoPulsadores = false;

    DynamicJsonDocument jsonBufferRecv(200);
    DeserializationError error = deserializeJson(jsonBufferRecv, texto);
    if (error) {
      Serial.println("Error al deserializar el JSON recibido");
    } else {

      if (jsonBufferRecv.containsKey("asiento")) {
        pulsador1 = jsonBufferRecv["asiento"];
      }
      if (jsonBufferRecv.containsKey("respaldo")) {
        pulsador2 = jsonBufferRecv["respaldo"];
      }
      if (jsonBufferRecv.containsKey("Estado")) {
        estado = jsonBufferRecv["Estado"];
      }
    }
  }

  M5.Lcd.setCursor(10, 170);
  M5.Lcd.print("Pulsador1: ");
  M5.Lcd.print(pulsador1 == 1 ? "Pulsado" : "No pulsado");

  M5.Lcd.setCursor(10, 190);
  M5.Lcd.print("Pulsador2: ");
  M5.Lcd.print(pulsador2 == 1 ? "Pulsado" : "No pulsado");

  M5.Lcd.setCursor(10, 210);
  M5.Lcd.print(estado);
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
    Serial.println(ldrValue);
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
  mostrarDatosUDP_PULSADORES();
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
void publicarDatos(float temp, float hum, int micValue, int ldrValue, int distancia, int pulsador1, int pulsador2) {
  if (!client.connected()) {
    conectarMQTT();
  }

  // Publicar temperatura
  char tempStr[10];
  dtostrf(temp, 6, 2, tempStr);
  client.publish("Sedora/sensores/temperatura", tempStr);

  // Publicar humedad
  char humStr[10];
  dtostrf(hum, 6, 2, humStr);
  client.publish("Sedora/sensores/humedad", humStr);

  // Publicar valor de sonido (ruido)
  int soundMessage = (micValue > thresholdHigh) ? 0 : 1;
  client.publish("Sedora/sensores/ruido", String(soundMessage).c_str());

  // Publicar valor de luz (luminosidad)
  int lightMessage = (ldrValue < 800) ? 1 : 0;
  client.publish("Sedora/sensores/luminosidad", String(lightMessage).c_str());

  // Publicar distancia (proximidad)
  char distStr[10];
  itoa(distancia, distStr, 10);
  client.publish("Sedora/sensores/proximidad", distStr);

  // Publicar pulsadores
  int estadoAsiento = pulsador1 ? 1 : 0;
  int estadoRespaldo = pulsador2 ? 1 : 0;

  client.publish("Sedora/sensores/presion1", String(estadoAsiento).c_str());
  client.publish("Sedora/sensores/presion2", String(estadoRespaldo).c_str());
}

//------------------------------------------------
//------------------------------------------------
void setup() {
  M5.begin();
  M5.Lcd.drawBitmap(0, 0, SEDORA1_COPY_SMALL_WIDTH, SEDORA1_COPY_SMALL_HEIGHT, sedora1_copy_Small);
  delay(3000);
  conectarWiFi();
  client.setServer(mqtt_server, mqtt_port);

//================LED================
  // Configurar MQTT LED
    client_led.setServer(mqtt_server_led, mqtt_port_led);
    client_led.setCallback(callbackMQTTLed);
    conectarMQTTLed();

    // Configurar LED
    pinMode(LED_PIN, OUTPUT);
    digitalWrite(LED_PIN, LOW);
//====================================

  // Conectar al broker MQTT
  conectarMQTT();
  
  iniciarUDP();
  dht.begin();
  verPantallaPrincipal(0, 0, 0, 0);
}
//------------------------------------------------
//------------------------------------------------
void loop() {
  M5.update();

   if (!client.connected()) {
    conectarMQTT();
  }
  
  //========LED=======
  if (!client_led.connected()) {
    conectarMQTTLed();
  }
  //========LED=======

  client.loop();
  client_led.loop();

  
  unsigned long currentMillis = millis();

  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  int micValue = analogRead(micPin);
  int ldrValue = analogRead(ldrPin);

  if (currentMillis - lastPublishTime >= 10000) {
    lastPublishTime = currentMillis;
    publicarDatos(temp, hum, micValue, ldrValue, distancia, pulsador1, pulsador2);
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
