#include "MsgServiceSerial.h"

enum {REQ, LIGHT, RB, SOMEONE, LIGHTSENS};

RemoteConfig* dbConfig;

SensorsReadings* sensors;

String parsedMsg[MSG_FIELDS];

MsgServiceSerial::MsgServiceSerial(SensorsReadings* sens, RemoteConfig* conf){
  this->sensors = sensors = sens;
  this->dbConfig = dbConfig = conf;
}

void MsgServiceSerial::sendMsg(String msg){
  Serial.println(msg);  
}

void clearMsg(){
  for (int i = 0; i < MSG_FIELDS; i ++ ){
    parsedMsg[i] = "";
  }
}

void MsgServiceSerial::receiveMsg() {
  int nchread = 0, i = 0;
  /* reading the content */
  while (Serial.available() && nchread <= MAX_LENGTH) {
    char ch = (char) Serial.read();
    switch(ch){
      case SEP:
        nchread = 0;
        i++;
        break;
      case END_COMM:
        dbConfig->setConfig(parsedMsg[REQ], parsedMsg[LIGHT], parsedMsg[RB]);
        sensors->setReadings(parsedMsg[SOMEONE], parsedMsg[LIGHTSENS]);
        Serial.flush();
        break;
      default:
        nchread ++;
        parsedMsg[i] += ch;
        break;
    }
  }
  clearMsg();
}