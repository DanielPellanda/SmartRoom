#include "SensorsReadings.h"

SensorsReadings::SensorsReadings(){
  this->isPresent = false;
  this->lightLvl = 0;
}

void SensorsReadings::setReadings(String presence, String lightLvl){
  presence == YES ? isPresent = true : isPresent = false;
  this->lightLvl = lightLvl.toInt();
}

int SensorsReadings::getLightLvl(){
 return lightLvl;
}

bool SensorsReadings::isSomeoneInRoom(){
  return isPresent;
}