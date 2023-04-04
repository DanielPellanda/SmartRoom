#ifndef __MSGSERVICEBT__
#define __MSGSERVICEBT__

#include "Arduino.h"
#include "SoftwareSerial.h"
#include "RemoteConfig.h"

#define NUM_PARAM 3
#define SEP ';'
#define END_COMM '\n'
#define TIMEOUT 10

class MsgServiceBT {
  public: 
    MsgServiceBT(int rxPin, int txPin, RemoteConfig* conf);

    void init();
    /**
    * receives and parses msg from the bluetooth module if present
    */
    void receiveMsg();
    /**
    * sends msg via bluetooth
    */
    void sendMsg(String msg);

  private:
    enum {REQ, LIGHT, RB};
    SoftwareSerial* channel = nullptr;
    String parsedMsg[NUM_PARAM];
    RemoteConfig* btConfig = nullptr;
    int failedComm;
  
    void clearMsg(){
      for (int i = 0; i < NUM_PARAM; i ++ ){
       parsedMsg[i] = "";
     }
   }
};

#endif
