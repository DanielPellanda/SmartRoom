#include "SensorsCheckTask.h"

LightCheckTask::LightCheckTask(int pinLs, int pinPir, int pinLed) {
  this->lightSens = new LightSensor(pinLs);
  this->pir = new Pir(pinPir);
  this->led = new Led(pinLed);
}

int* LightCheckTask::getLightLevel() {
  return currLight;
}

int* LightCheckTask::isSomeoneInRoom() {
  return someone;
}

void LightCheckTask::setup() {
  *currLight = 0;
  *someone = false;
  wasDetected = false;
  pir->calibrate();
  yield();
}

void LightCheckTask::loop() {
  *currLight = lightSens->measureLightLevel();
  if(pir->isMovementDetected() && !wasDetected){
    led->isOff() ? led->turnOn() : led->turnOff();
    wasDetected = true;
  }else if(!pir->isMovementDetected() && wasDetected) {
    wasDetected = false;
  }
  led->isOn() ? *someone  = true : *someone = false;
}
