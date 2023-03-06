#ifndef __SENSORS_READINGS__
#define __SENSORS_READINGS__

class SensorsReadings {
  bool isPresent = false;
  bool lightSS = false;
  int lightLvl = 0;
  
  public:
    SensorsReadings();

    void setReadings(bool isPresent, bool lightSS, int lightLvl);
    int getLightLvl();
    bool isLightSysActive();
    bool isSomeoneInRoom();
};

#endif