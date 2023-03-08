#include "CommunicationTask.h"

CommunicationTask::CommunicationTask (RoomState* currState, int rxPin, int txPin){
  this->currState = currState;
  this->btCommChannel = new MsgServiceBT(rxPin, txPin);
  this->serialCommChannel = new MsgServiceSerial();
}

void CommunicationTask::init(int period) {
  Task::init(period);
}

void CommunicationTask::tick() {
  // parser->parse(btCommChannel->receiveMsg());
  // parser->parse(serialCommChannel->isMsgAvailable());

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
