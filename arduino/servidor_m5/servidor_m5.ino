#include <M5Stack.h>
#include "DHT.h"
#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>

// Definiciones
#define DHTPIN 26
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

#define BLANCO 0xFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x009774
#define AZUL 0x001F

const char* ssid = "***************";
const char* password = "***********";

char texto[200];
boolean recibido = 0;
AsyncUDP udp;

// Función para mostrar el estado de la conexión WiFi
void conectarWiFi() {
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(50, 100);
  M5.Lcd.setTextColor(VERDE);
  M5.Lcd.println("[Servidor] Arrancando...");

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    M5.Lcd.fillScreen(NEGRO);
    M5.Lcd.setTextColor(ROJO);
    M5.Lcd.println("[Servidor] Error al conectar a WiFi!");
    while (1) {
      delay(1000);
    }
  }

  M5.Lcd.fillScreen(NEGRO);
  M5.Lcd.setTextColor(VERDE);
  M5.Lcd.println("[Servidor] Conexion OK.");
}

// Función para escuchar paquetes UDP
void iniciarUDP() {
  if (udp.listen(1234)) {
    udp.onPacket([](AsyncUDPPacket packet) {
      int i = 200;
      while (i--) {
        *(texto + i) = *(packet.data() + i);
      }
      recibido = 1;
    });
  }
}

// Función para mostrar los datos de temperatura y humedad
void mostrarHumedadYTemperatura() {
  float h = dht.readHumidity();
  float t = dht.readTemperature();

  M5.Lcd.setCursor(0, 0);
  M5.Lcd.print(F("Humedad: "));
  M5.Lcd.print(h);
  M5.Lcd.print(F("%"));

  M5.Lcd.setCursor(0, 30);
  M5.Lcd.print(F("Temperatura: "));
  M5.Lcd.print(t);
  M5.Lcd.print(F("°C"));
}

// Función para procesar los datos recibidos por UDP
void procesarPaqueteUDP() {
  if (recibido) {
    recibido = 0;
    StaticJsonDocument<200> jsonBufferRecv;
    DeserializationError error = deserializeJson(jsonBufferRecv, texto);
    if (error) return;

    int distancia = jsonBufferRecv["Distancia"];
    bool alerta = jsonBufferRecv["Alerta"];

    M5.Lcd.setCursor(0, 60);
    M5.Lcd.setTextColor(BLANCO);
    M5.Lcd.print("[Servidor] Distancia: ");
    M5.Lcd.printf("%d cm", distancia);

    if (alerta) {
      M5.Lcd.println(" - ¡ALERTA! Distancia crítica.");
      M5.Speaker.tone(500, 200);
      delay(500);
      M5.Speaker.mute();
      M5.Lcd.fillCircle(160, 150, 20, ROJO);
    } else {
      M5.Lcd.println(" - Distancia segura.");
      M5.Speaker.mute();
    }
  }
}

void setup() {
  M5.begin();
  conectarWiFi();
  delay(2000);
  M5.Lcd.fillScreen(NEGRO);
  iniciarUDP();
  dht.begin();
}

void loop() {
  M5.Lcd.clear();
  mostrarHumedadYTemperatura();
  procesarPaqueteUDP();
  delay(4000);
}
