#include "SensorsReadings.h"

void SensorsReadings::setReadings(bool isPresent, int lightLvl){
  this->isPresent = isPresent;
  this->lightLvl = lightLvl;
}

int SensorsReadings::getLightLvl(){
 return lightLvl;
}

bool SensorsReadings::isSomeoneInRoom(){
  return isPresent;
}