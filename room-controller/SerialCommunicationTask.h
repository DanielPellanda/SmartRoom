#ifndef __SERIAL_COMMUNICATION_TASK__
#define __SERIAL_COMMUNICATION_TASK__

#include "Task.h"
#include "SerialCommunication.h"

#define SEP ";"


class SerialCommunicationTask : public Task {

  public:
    SerialCommunicationTask(AlarmState* currState);

    void init(int period);
    void tick();
};

#endif