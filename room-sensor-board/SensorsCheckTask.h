#ifndef __LIGHT_TASK__
#define __LIGHT_TASK__

#include "LightSensor.h"
#include "Pir.h"
#include "Led.h"


class LightCheckTask : public Task {
	LightSensor* lightSens;
  Pir* pir;
  Led* led;
  int* currLight;
  bool wasDetected;
  bool* someone;

	public:
		LightCheckTask(int pinLs, int pinPir, int pinLed);
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
