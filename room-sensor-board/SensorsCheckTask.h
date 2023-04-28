#ifndef __LIGHT_TASK__
#define __LIGHT_TASK__

#include "LightSensor.h"
#include "Pir.h"
#include "Led.h"

#define TIMEOUT 2000 //msec

class SensorsCheckTask {
	LightSensor* lightSens = nullptr;
  Pir* pir = nullptr;
  Led* led = nullptr;
  int lastDetection, period, currLight;
  bool someone;

	public:
		SensorsCheckTask(int pinLs, int pinPir, int pinLed);
    /**
    * returns the current light in percentage from 0 to 100
    */
		int getLightLevel();
    /**
    * returns true if someone is in the room
    */
    bool isSomeoneInRoom();
    void init(int period);
    void tick();
};

#endif