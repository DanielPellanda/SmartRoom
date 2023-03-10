#ifndef __SENSORS_READINGS__
#define __SENSORS_READINGS__

#include "Arduino.h"

#define YES "1"

class SensorsReadings {
  bool isPresent = false;
  int lightLvl = 0;
  
  public:
    SensorsReadings();

    void setReadings(String presence, String lightLvl);
    int getLightLvl();
    bool isSomeoneInRoom();
};

#endif