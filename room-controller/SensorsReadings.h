#ifndef __SENSORS_READINGS__
#define __SENSORS_READINGS__

class SensorsReadings {
  bool isPresent = false;
  int lightLvl = 0;
  
  public:
    SensorsReadings();

    void setReadings(bool isPresent, int lightLvl);
    int getLightLvl();
    bool isSomeoneInRoom();
};

#endif