#ifndef __LIGHT_TASK__
#define __LIGHT_TASK__

#include "LightSensor.h"
#include "Pir.h"
#include "Led.h"

#define TIMEOUT 2000 //msec

class SensorsCheckTask {
	LightSensor* lightSens;
  Pir* pir;
  Led* led;
  int* currLight;
  int lastDetection, period;
  bool* someone;

	public:
		SensorsCheckTask(int pinLs, int pinPir, int pinLed);
    void init(int period);
    void tick();
    /**
    * returns the current light in percentage from 0 to 100
    */
		int* getLightLevel();
    /**
    * returns a pointer to the variable that keeps track 
    * of the presence of someone in the room
    */
    bool* isSomeoneInRoom();
};

#endif