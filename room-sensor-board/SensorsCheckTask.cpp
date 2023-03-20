#include "SensorsCheckTask.h"

SensorsCheckTask::SensorsCheckTask(int pinLs, int pinPir, int pinLed){
  this->lightSens = new LightSensor(pinLs);
  this->pir = new Pir(pinPir);
  this->led = new Led(pinLed);
}

int* SensorsCheckTask::getLightLevel() {
  return currLight;
}

bool* SensorsCheckTask::isSomeoneInRoom() {
  return someone;
}

void SensorsCheckTask::init(int period) {
  this->period = period;
  *currLight = 0;
  *someone = false;
  lastDetection = 0;
  pir->calibrate();
}

void SensorsCheckTask::tick() {
  *currLight = lightSens->measureLightLevel();
  if (!pir->isMovementDetected()){
    if(led->isOn() && lastDetection > TIMEOUT){
      led->turnOff();
    }
    lastDetection += period;
  } else if (led->isOff()) {
    lastDetection = 0;
    led->turnOn();
  }
  led->isOn() ? *someone  = true : *someone = false;
}
