#include <M5Stack.h>
#include "HX711.h"

#define DT_PIN 26
#define SCK_PIN 25

HX711 scale;

void setup() {
  M5.begin();
  Serial.begin(115200);

  scale.begin(DT_PIN, SCK_PIN);

  scale.set_scale(2280.f);  
  scale.tare();          
  M5.Lcd.clear();
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.print("Calibrando...");
}

void loop() {
  float peso = scale.get_units(10);
  peso = abs(peso);

  M5.Lcd.clear();
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.print("Peso: ");
  M5.Lcd.print(peso, 2);  
  M5.Lcd.print(" g");

  if (peso > 20) {
    M5.Lcd.setCursor(10, 40);
    M5.Lcd.print("Sentado correctamente");
  } else {
    M5.Lcd.setCursor(10, 40);
    M5.Lcd.print("Sentado incorrectamente");
  }

  delay(2000);
}
