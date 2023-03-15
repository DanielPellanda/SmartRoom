#ifndef __LIGHT_SENSOR__
#define __LIGHT_SENSOR__

#define MIN_LIGHT_VALUE 0
#define MAX_LIGHT_VALUE 1000

class LightSensor {
  int pin;

  public:
    LightSensor(int pin);
    /**
    * Calculates the light level from the analog input value
    */
    int measureLightLevel();
};

#endif