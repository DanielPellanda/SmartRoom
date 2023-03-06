#include "ClockTask.h"

ClockTask::ClockTask(RoomState* currState, int timeBand){
  this->currState = currState;
  this->clock = new Clock(0);
}
void ClockTask::init(int period) {
  Task::init(period);
}

void ClockTask::tick() {
  int currTime = clock->nextTimeBand();
  switch (*currState){
    case AUTO:
      if (isSleepTime(currTime)){
        *currState = SLEEP;
      }
      break;
    case SLEEP:
      if (!isSleepTime(currTime)){
        *currState = AUTO;
      }
      break;
    default:
      break;
  }
}

int ClockTask::getClockTime(){
  return this->clock->getTime();
}