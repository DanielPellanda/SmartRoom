#include "CommunicationTask.h"

CommunicationTask::CommunicationTask (RoomState* currState){
  this->currState = currState;
}

void CommunicationTask::init(int period) {
    Task::init(period);
    //MsgService.init();
}

void CommunicationTask::tick() {
    switch (*this->currState){
    case AUTO:
      break;
    case BLUETOOTH:
      break;
    case DASHBOARD:
      break;
    default:
      break;
    }
}
