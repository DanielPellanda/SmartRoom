#ifndef __LIGHTING_TASK__
#define __LIGHTING_TASK__

#include "Task.h"
#include "SensorsReadings.h"
#include "Led.h"

#define DARK 50

class LightingTask : public Task {
  RoomState* currState = nullptr;
  SensorsReadings* room = nullptr;
  Led* led;
    
  public:
    LightingTask(RoomState* currState, SensorsReadings* room, int ledPin);

    void init(int period);
    void tick();
};

#endif