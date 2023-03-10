#include "CommunicationTask.h"

CommunicationTask::CommunicationTask(RoomState* currState, int rxPin, int txPin,
    RemoteConfig* btConfig, RemoteConfig* dbConfig, SensorsReadings* sens){
  this->currState = currState;
  this->btCommChannel = new MsgServiceBT(rxPin, txPin, btConfig);
  this->serialCommChannel = new MsgServiceSerial(sens, dbConfig);
  this->btConfig = btConfig;
  this->dbConfig = dbConfig;
  this->sens = sens;
}

void CommunicationTask::init(int period) {
  Task::init(period);
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
  btCommChannel->sendMsg(msg);
  serialCommChannel->sendMsg(msg);
}
