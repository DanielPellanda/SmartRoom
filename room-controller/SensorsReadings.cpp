#include "SensorsReadings.h"

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