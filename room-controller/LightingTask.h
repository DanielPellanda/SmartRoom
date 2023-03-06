#ifndef __LIGHTING_TASK__
#define __LIGHTING_TASK__

#include "Task.h"
#include "SensorsReadings.h"


#define SEP ";"


class LightingTask : public Task {
  RoomState* currState = nullptr;
  SensorsReadings* room = nullptr;
    
  public:
    LightingTask(RoomState* currState, SensorsReadings* room);

    void init(int period);
    void tick();
};

#endif