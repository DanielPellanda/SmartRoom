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

void SensorsCheckTask::init(int period) {
  this->period = period;
  someone = false;
  lastDetection = currLight = 0;
  pir->calibrate();
  led->turnOff();
}

void SensorsCheckTask::tick() {
  currLight = lightSens->measureLightLevel();
  if (!pir->isMovementDetected()){
    Serial.println("not detected");
    if(led->isOn() && lastDetection > TIMEOUT){
      led->turnOff();
    }
    lastDetection += period;
  } else {
    lastDetection = 0;
    if (led->isOff()){
      led->turnOn();
    }
  }
  led->isOn() ? someone  = true : someone = false;
}
