#include "SensorsReadings.h"

void SensorsReadings::setReadings(bool isPresent, bool lightSS, int lightLvl){
  this->isPresent = isPresent;
  this->lightSS = lightSS;
  this->lightLvl = lightLvl;
}

int SensorsReadings::getLightLvl(){
 return lightLvl;
}

bool SensorsReadings::isLightSysActive(){
 return lightSS;
}

bool SensorsReadings::isSomeoneInRoom(){
  return isPresent;
}