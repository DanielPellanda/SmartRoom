#ifndef __COMMUNICATION_TASK__
#define __COMMUNICATION_TASK__

#include "Task.h"
#include "MsgServiceSerial.h"
#include "MsgServiceBT.h"
#include "RemoteConfig.h"
#include "SensorsReadings.h"
#include "Clock.h"
#include "RoomControlTask.h"

class CommunicationTask : public Task {
  RoomState* currState = nullptr;
  MsgServiceBT* btCommChannel = nullptr;
  MsgServiceSerial* serialCommChannel = nullptr;
  RemoteConfig* btConfig = nullptr;
  RemoteConfig* dbConfig = nullptr;
  SensorsReadings* sens = nullptr;
  Clock* clock = nullptr;
  
  public:
    CommunicationTask(RoomState* currState, int rxPin, int txPin);

    void init(int period, ClockTask* clockTask);
    void tick();
    RemoteConfig* getBTConfig();
    RemoteConfig* getDBConfig();
    SensorsReadings* getSensorsReadings();
};

#endif