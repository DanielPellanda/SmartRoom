#include "SensorsCheckTask.h"

SensorsCheckTask::SensorsCheckTask(int pinLs, int pinPir, int pinLed){
  this->lightSens = new LightSensor(pinLs);
  this->pir = new Pir(pinPir);
  this->led = new Led(pinLed);
}

int SensorsCheckTask::getLightLevel() {
  return currLight;
}

bool SensorsCheckTask::isSomeoneInRoom() {
  return someone;
}

void SensorsCheckTask::init() {
  someone = false;
  currLight = 0;
  pir->calibrate();
  led->turnOff();
}

void SensorsCheckTask::tick() {
  currLight = lightSens->measureLightLevel();
  pir->isMovementDetected() ? led->turnOn() : led->turnOff();
  led->isOn() ? someone  = true : someone = false;
}
