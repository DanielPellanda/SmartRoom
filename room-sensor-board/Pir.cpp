#include "Pir.h"

Pir::Pir(int pirPin) {
  pinMode(pirPin, INPUT);
}

bool Pir::isMovementDetected() {
  return digitalRead(pirPin) == HIGH;
}

Pir::calibrate(){
  for (int i=0; i< CALIBRATION_TIME; i++) {
    delay(1000);
  }
}
