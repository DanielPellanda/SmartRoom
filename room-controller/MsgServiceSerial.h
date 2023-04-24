#ifndef __MSG_SERVICE_SERIAL__
#define __MSG_SERVICE_SERIAL__

#include "Arduino.h"
#include "RemoteConfig.h"
#include "SensorsReadings.h"

#define MSG_FIELDS 5
#define SEP ';'
#define END_COMM '\n'
#define MAX_LENGTH 3

class MsgServiceSerial {
  public:
    RemoteConfig* dbConfig = nullptr;
    SensorsReadings* sensors = nullptr;

    MsgServiceSerial(SensorsReadings* sens, RemoteConfig* conf);
    /**
    * sends msg on the serial line
    */
    void sendMsg(String msg);
    /**
    * receives a message from the serial line
    */
    void receiveMsg();
};

#endif