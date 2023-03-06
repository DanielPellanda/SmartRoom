#ifndef __CLOCK_TASK__
#define __CLOCK_TASK__

#include "Task.h"
#include "Clock.h"

#define SLEEP_START 19
#define SLEEP_FINISH 8

class ClockTask : public Task {
  RoomState* currState = nullptr;
  Clock* clock = nullptr;
  
  /**
  * returns true if a timeband is a sleep band
  */
  bool isSleepTime(int timeBand){
    return timeBand > SLEEP_START || timeBand <SLEEP_FINISH ;
  }

  public:
    ClockTask(RoomState* currState, int timeBand);

    void init(int period);
    int getClockTime();
    void tick();
};

#endif