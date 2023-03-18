#include "LightSensor.h"

LightSensor::LightSensor(int pin) {
  this->pin = pin;
}

int LightSensor::calculateLightLevel() {
  return map(analogRead(pin),MIN_LIGHT_VALUE,MAX_LIGHT_VALUE,0,100);
}
