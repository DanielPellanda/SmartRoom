#include "CommunicationTask.h"

CommunicationTask::CommunicationTask(RoomState* currState, int rxPin, int txPin){
  this->currState = currState;
  this->btConfig = new RemoteConfig();
  this->dbConfig = new RemoteConfig();
  this->sens = new SensorsReadings();
  this->btCommChannel = new MsgServiceBT(rxPin, txPin, this->btConfig);
  this->serialCommChannel = new MsgServiceSerial(sens, this->dbConfig);
}

void CommunicationTask::init(int period, ClockTask* clockTask, int* servoAngle, bool* lights) {
  Task::init(period);
  this->clock = clockTask->getClock();
  this->servoAngle = servoAngle;
  this->lights = lights;
}

void CommunicationTask::tick() {
  String msg, light;
  btCommChannel->receiveMsg();
  serialCommChannel->receiveMsg();
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

  /* pack informations */
  *lights ? light = "1" : light = "0";
  msg = *currState + sep + clock->getHour() + sep + clock->getMinute()
    + sep + light + sep + *servoAngle;

  /* send informations */
  btCommChannel->sendMsg(msg);
  serialCommChannel->sendMsg(msg);
}

RemoteConfig* CommunicationTask::getBTConfig(){
  return this->btConfig;
}

RemoteConfig* CommunicationTask::getDBConfig(){
  return this->dbConfig;
}

SensorsReadings* CommunicationTask::getSensorsReadings(){
  return this->sens;
}