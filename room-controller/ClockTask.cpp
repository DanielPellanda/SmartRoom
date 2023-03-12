#include "ClockTask.h"

ClockTask::ClockTask(RoomState* currState){
  this->currState = currState;
  this->clock = new Clock(START);
}
void ClockTask::init(int period) {
  Task::init(period);
}

void ClockTask::tick() {
  clock->clockTick();
}

Clock* ClockTask::getClock(){
  return this->clock;
}
