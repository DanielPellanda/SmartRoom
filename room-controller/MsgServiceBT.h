#ifndef __MSGSERVICEBT__
#define __MSGSERVICEBT__

#include "Arduino.h"
#include "SoftwareSerial.h"
#include "RemoteConfig.h"

#define NUM_PARAM 3
#define SEP ';'
#define END_COMM '\n'

class MsgServiceBT {
    
public: 
  MsgServiceBT(int rxPin, int txPin, RemoteConfig* conf);

  void init();  
  void receiveMsg();
  bool sendMsg(String msg);

private:
  enum {REQ, LIGHT, RB};

  SoftwareSerial* channel = nullptr;
  String parsedMsg[NUM_PARAM];
  RemoteConfig* btConfig = nullptr;
  
  void clearMsg(){
    for (int i = 0; i < NUM_PARAM; i ++ ){
      parsedMsg[i] = "";
    }
  }
};

#endif