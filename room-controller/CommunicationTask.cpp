#include "CommunicationTask.h"

CommunicationTask::CommunicationTask(RoomState* currState, int rxPin, int txPin){
  this->currState = currState;
  this->btConfig = new RemoteConfig();
  this->dbConfig = new RemoteConfig();
  this->sens = new SensorsReadings();
  this->btCommChannel = new MsgServiceBT(rxPin, txPin, this->btConfig);
  this->serialCommChannel = new MsgServiceSerial(sens, this->dbConfig);
}

void CommunicationTask::init(int period, ClockTask* clockTask) {
  Task::init(period);
  this->clock = clockTask->getClock();
}

void CommunicationTask::tick() {
  String msg;
  btCommChannel->receiveMsg();
  switch (*currState){
    case AUTO:
      if(btConfig->isCtrlReq()){
        *currState = BLUETOOTH;
      } else if(dbConfig->isCtrlReq()){
        *currState = DASHBOARD;
      }
      break;
    case BLUETOOTH:
      if(!btConfig->isCtrlReq()){
        *currState = AUTO;
      }
      break;
    case DASHBOARD:
      if(!dbConfig->isCtrlReq()){
        *currState = AUTO;
      }
      break;
    default:
      break;
  }
  msg = *currState + SEP + clock->getHour() + SEP + clock->getMinute() ;
  btCommChannel->sendMsg(msg);
  serialCommChannel->sendMsg(msg);
}
