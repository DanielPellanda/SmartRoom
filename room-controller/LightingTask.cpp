#include "LightingTask.h"

LightingTask::LightingTask (RoomState* currState, SensorsReadings* room){
  this->currState = currState;
  this->room = room;
}

void LightingTask::init(int period) {
    Task::init(period);
}

void LightingTask::tick() {
    switch (*this->currState){
    case AUTO:
      if(room->isSomeoneInRoom()){

      } else {
        
      }
      break;
    case BLUETOOTH:
      break;
    case DASHBOARD:
      break;
    default:
      break;
    }
}