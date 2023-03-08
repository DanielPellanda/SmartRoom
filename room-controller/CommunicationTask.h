#ifndef __COMMUNICATION_TASK__
#define __COMMUNICATION_TASK__

#include "Task.h"
#include "MsgServiceSerial.h"
#include "MsgServiceBT.h"

#define SEP ";"

class CommunicationTask : public Task {
  RoomState* currState = nullptr;
  MsgServiceBT* btCommChannel = nullptr;
  MsgServiceSerial* serialCommChannel = nullptr;
  
  public:
    CommunicationTask(RoomState* currState, int rxPin, int txPin);

    void init(int period);
    void tick();
};

#endif