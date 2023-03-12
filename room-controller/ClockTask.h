#ifndef __CLOCK_TASK__
#define __CLOCK_TASK__

#include "Task.h"
#include "Clock.h"

#define START 0

class ClockTask : public Task {
  RoomState* currState = nullptr;
  Clock* clock = nullptr;

  public:
    ClockTask(RoomState* currState);

   /**
    * returns the virtual clock
    */
    Clock* getClock();
    
    void init(int period);
    void tick();
};

#endif