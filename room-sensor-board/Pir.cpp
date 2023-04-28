#include "Pir.h"

Pir::Pir(int pirPin) {
  this->pin = pirPin;
  pinMode(pirPin, INPUT);
}

bool Pir::isMovementDetected() {
  return digitalRead(pin) == HIGH;
}

void Pir::calibrate() {
  for (int i=0; i< CALIBRATION_TIME; i++) {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("\nPir calibration complete");
}
