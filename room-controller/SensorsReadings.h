#ifndef __SENSORS_READINGS__
#define __SENSORS_READINGS__

#include "Arduino.h"

#define YES 1

class SensorsReadings {
  bool isPresent;
  long lightLvl;
  
  public:
    SensorsReadings();
    /**
    * interprets the input strings taken from serial line and assigns them to internal fields
    */
    void setReadings(String presence, String lightLvl);
    /**
    * returns the current light level in the room from 0 to 100 %
    */
    int getLightLvl();
    /**
    * true if someone is in the room
    */
    bool isSomeoneInRoom();
};

#endif