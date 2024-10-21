#include <M5Stack.h>  // Incluye la biblioteca M5Stack

int ldrPin = 36;  // Pin donde conectaste el sensor LDR
int prevState = -1;  // Para controlar el cambio de estado

void setup() {
  M5.begin();  // Inicializa la pantalla y el sistema del M5Stack
  pinMode(ldrPin, INPUT);  // Configura el pin del LDR como entrada
  M5.Lcd.fillScreen(BLACK);  // Limpia la pantalla inicial
  M5.Lcd.setTextSize(3);  // Ajusta el tamaño del texto
  Serial.begin(9600);  // Inicializa el monitor serial para depuración
}

void loop() {
  int ldrValue = analogRead(ldrPin);  // Lee el valor analógico del LDR
  int currentState = (ldrValue < 800) ? 0 : 1;  // Determina si la luz es adecuada (0) o inadecuada (1)

  // Solo actualiza la pantalla si hay un cambio de estado
  if (currentState != prevState) {
    M5.Lcd.fillScreen(BLACK);  // Limpia la pantalla
    M5.Lcd.setCursor(50, 100);  // Posiciona el texto
    if (currentState == 0) {
      M5.Lcd.println("Luminosidad adecuada");  // Muestra texto si la luz es adecuada
      Serial.println("Estado: Luminosidad adecuada");
    } else {
      M5.Lcd.println("Luminosidad inadecuada");  // Muestra texto si la luz es inadecuada
      Serial.println("Estado: Luminosidad inadecuada");
    }
    prevState = currentState;  // Actualiza el estado
  }

  delay(500);  // Pausa de 0.5 segundos para evitar cambios rápidos
}
