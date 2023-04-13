#ifndef __COMMUNICATION_TASK__
#define __COMMUNICATION_TASK__

#include "Task.h"
#include "MsgServiceSerial.h"
#include "MsgServiceBT.h"
#include "RemoteConfig.h"
#include "SensorsReadings.h"
#include "ClockTask.h"

class CommunicationTask : public Task {
  RoomState* currState = nullptr;
  MsgServiceBT* btCommChannel = nullptr;
  MsgServiceSerial* serialCommChannel = nullptr;
  RemoteConfig* btConfig = nullptr;
  RemoteConfig* dbConfig = nullptr;
  SensorsReadings* sens = nullptr;
  Clock* clock = nullptr;
  int* servoAngle;
  bool* lights;
  const String sep = ";";
  
  public:
    CommunicationTask(RoomState* currState, int rxPin, int txPin);

    void init(int period, ClockTask* clockTask, int* servoAngle, bool* lights);
    void tick();
    /**
    * returns remote configuration received via bluetooth
    */
    RemoteConfig* getBTConfig();
    /**
    * returns remote configuration received via serial line
    */
    RemoteConfig* getDBConfig();
    /**
    * returns the readings of the sensor board from the serial line 
    */
    SensorsReadings* getSensorsReadings();
};

#endif