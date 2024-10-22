#include <M5Stack.h>  // Incluye la biblioteca M5Stack

int micPin = 36;  // Pin donde conectaste la salida analógica (AO) del KY-037
int thresholdHigh = 600;  // Umbral alto para sonido inadecuado
int thresholdLow = 400;   // Umbral bajo para sonido adecuado
int prevState = -1;  // Para controlar el cambio de estado

void setup() {
  M5.begin();  // Inicializa la pantalla del M5Stack
  pinMode(micPin, INPUT);  // Configura el pin del micrófono como entrada
  M5.Lcd.fillScreen(BLACK);  // Limpia la pantalla inicial
  M5.Lcd.setTextSize(3);  // Ajusta el tamaño del texto
  Serial.begin(9600);  // Inicializa el monitor serial para depuración
}

void loop() {
  int micValue = analogRead(micPin);  // Lee el valor analógico del micrófono
  int currentState;

  // Aplica histeresis para evitar cambios rápidos
  if (micValue > thresholdHigh) {
    currentState = 1;  // Nivel de sonido inadecuado
  } else if (micValue < thresholdLow) {
    currentState = 0;  // Nivel de sonido adecuado
  } else {
    currentState = prevState;  // Mantén el estado anterior si está en el margen intermedio
  }

  // Solo actualiza la pantalla si hay un cambio de estado
  if (currentState != prevState) {
    M5.Lcd.fillScreen(BLACK);  // Limpia la pantalla
    M5.Lcd.setCursor(50, 100);  // Posiciona el texto
    if (currentState == 1) {
      M5.Lcd.println("Nivel de sonido inadecuado");  // Muestra texto si el sonido es inadecuado
      Serial.println("Estado: Nivel de sonido inadecuado");
    } else {
      M5.Lcd.println("Nivel de sonido adecuado");  // Muestra texto si el sonido es adecuado
      Serial.println("Estado: Nivel de sonido adecuado");
    }
    prevState = currentState;  // Actualiza el estado
  }

  delay(500);  // Pausa de 0.5 segundos para evitar cambios rápidos
}
