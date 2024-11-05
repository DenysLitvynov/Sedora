#include <M5Stack.h>
#include "DHT.h"
#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>
#include "sedora1_map.h"

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


#define BLANCO 0xFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x009774
#define AZUL 0x001F
const char* ssid = "*******";
const char* password = "********";

char texto[200];
boolean recibido = false;
AsyncUDP udp;
int currentScreen = 0;

// Función para mostrar el estado de la conexión WiFi
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
void iniciarUDP() {
    if (udp.listen(1234)) {
        udp.onPacket([](AsyncUDPPacket packet) {
            int i = 200;
            while (i--) *(texto + i) = *(packet.data() + i);
            recibido = true;
        });
    }
}

//------------------------------------------------
void mostrarDatosUDP() {
    if (recibido) {
        recibido = false;
        DynamicJsonDocument jsonBufferRecv(200);
        DeserializationError error = deserializeJson(jsonBufferRecv, texto);
        if (error) return;

        int distancia = jsonBufferRecv["Distancia"];
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
void mostrarHumedad(int x, int y) {
    float h = dht.readHumidity();
    M5.Lcd.setCursor(x, y);
    M5.Lcd.print("Humedad: ");
    M5.Lcd.print(h);
    M5.Lcd.print("%");
}

//------------------------------------------------
void mostrarTemperatura(int x, int y) {
    float t = dht.readTemperature();
    M5.Lcd.setCursor(x, y);
    M5.Lcd.print("Temperatura: ");
    M5.Lcd.print(t);
    M5.Lcd.print("°C");
}

//------------------------------------------------
void mostrarNivelDeSonido(int x, int y) {
    int micValue = analogRead(micPin);
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
void mostrarLuminosidad(int x, int y) {
    int ldrValue = analogRead(ldrPin);
    M5.Lcd.setCursor(x, y);
    if (ldrValue < 800) {
        M5.Lcd.println("Luminosidad: Adecuada");
    } else {
        M5.Lcd.println("Luminosidad: Inadecuada");
    }
}

//------------------------------------------------
void verPantallaPrincipal() {
    M5.Lcd.clear(BLANCO);
    M5.Lcd.setTextColor(NEGRO);
    M5.Lcd.setCursor(10, 10);
    M5.Lcd.setTextSize(2);
    M5.Lcd.println("Pantalla Principal");

    mostrarHumedad(10, 110);
    mostrarTemperatura(10, 90);
    mostrarNivelDeSonido(10, 50);
    mostrarLuminosidad(10, 70);
    mostrarDatosUDP();
}

//------------------------------------------------
void verPantallaTemp() {
    M5.Lcd.clear(AZUL);
    M5.Lcd.setTextColor(BLANCO);
    M5.Lcd.setTextSize(2);

    M5.Lcd.setCursor(10, 20);
    M5.Lcd.println("Temperatura");

    mostrarTemperatura(10, 70);
}

//------------------------------------------------
void verPantallaHum() {
    M5.Lcd.clear(AZUL);
    M5.Lcd.setTextColor(BLANCO);
    M5.Lcd.setTextSize(2);

    M5.Lcd.setCursor(10, 20);
    M5.Lcd.println("Humedad");

    mostrarHumedad(10, 70);
}

//------------------------------------------------
void verPantallaSon() {
    M5.Lcd.clear(ROJO);
    M5.Lcd.setTextColor(BLANCO);
    M5.Lcd.setTextSize(2);
    M5.Lcd.setCursor(10, 20);
    M5.Lcd.println("Nivel de Sonido");

    mostrarNivelDeSonido(10, 70);
}

//------------------------------------------------
void verPantallaLuz() {
    M5.Lcd.clear(VERDE);
    M5.Lcd.setTextColor(NEGRO);
    M5.Lcd.setTextSize(2);

    M5.Lcd.setCursor(10, 20);
    M5.Lcd.println("Luminosidad");

    mostrarLuminosidad(10, 70);
}

//------------------------------------------------
void verPantallaDist() {
    M5.Lcd.clear(NEGRO);
    M5.Lcd.setTextColor(BLANCO);
    M5.Lcd.setTextSize(2);

    M5.Lcd.setCursor(10, 20);
    M5.Lcd.println("Distancia");

    mostrarDatosUDP(); 
}

//------------------------------------------------
void setup() {
    M5.begin();
    M5.Lcd.drawBitmap(0, 0, SEDORA1_COPY_SMALL_WIDTH, SEDORA1_COPY_SMALL_HEIGHT, sedora1_copy_Small);
    delay(3000);
    conectarWiFi();
    delay(2000);
    iniciarUDP();
    dht.begin();
    verPantallaPrincipal();
}

//------------------------------------------------
void loop() {
    M5.update();

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
            verPantallaPrincipal();
            break;
        case 1:
            verPantallaTemp();
            break;
        case 2:
            verPantallaSon();
            break;
        case 3:
            verPantallaLuz();
            break;
        case 4:
            verPantallaDist();
            break;
        case 5:
            verPantallaHum();
            break;
    }
    delay(2000);
}
