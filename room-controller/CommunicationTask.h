#ifndef __COMMUNICATION_TASK__
#define __COMMUNICATION_TASK__

#include "Task.h"
//#include "SerialCommChannel.h"

#define SEP ";"


class CommunicationTask : public Task {
  RoomState* currState = nullptr;
  
  public:
    CommunicationTask(RoomState* currState);

    void init(int period);
    void tick();
};

#endif