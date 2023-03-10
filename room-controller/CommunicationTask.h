#ifndef __COMMUNICATION_TASK__
#define __COMMUNICATION_TASK__

#include "Task.h"
#include "MsgServiceSerial.h"
#include "MsgServiceBT.h"
#include "RemoteConfig.h"
#include "SensorsReadings.h"

class CommunicationTask : public Task {
  RoomState* currState = nullptr;
  MsgServiceBT* btCommChannel = nullptr;
  MsgServiceSerial* serialCommChannel = nullptr;
  RemoteConfig* btConfig = nullptr;
  RemoteConfig* dbConfig = nullptr;
  SensorsReadings* sens = nullptr;
  
  public:
    CommunicationTask(RoomState* currState, int rxPin, int txPin,
      RemoteConfig* btConfig, RemoteConfig* dbConfig, SensorsReadings* sens);

    void init(int period);
    void tick();
};

#endif