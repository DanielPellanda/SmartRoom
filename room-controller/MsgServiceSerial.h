#ifndef __MSG_SERVICE_SERIAL__
#define __MSG_SERVICE_SERIAL__

#include "Arduino.h"
#include "RemoteConfig.h"
#include "SensorsReadings.h"

#define MSG_FIELDS 5
#define SEP ';'
#define END_COMM '\n'

class MsgServiceSerial {
  public:

    MsgServiceSerial(SensorsReadings* sens, RemoteConfig* conf);
    /**
    * sends msg on the serial line
    */
    void sendMsg(String msg);
    /**
    * receives a message from the serial line
    */
    void receiveMsg();

  private:
    enum {REQ, LIGHT, RB, SOMEONE, LIGHTSENS};
    RemoteConfig* dbConfig = nullptr;
    SensorsReadings* sensors = nullptr;
    String parsedMsg[MSG_FIELDS];
    int index = 0;

    void clearMsg(){
      for (int i = 0; i < MSG_FIELDS; i ++ ){
        parsedMsg[i] = "";
      }
    }
};

#endif