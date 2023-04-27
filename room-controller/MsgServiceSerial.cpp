#include "MsgServiceSerial.h"

MsgServiceSerial::MsgServiceSerial(SensorsReadings* sens, RemoteConfig* conf){
  this->sensors = sens;
  this->dbConfig = conf;
}

void MsgServiceSerial::sendMsg(String msg){
  Serial.println(msg);
}

void MsgServiceSerial::receiveMsg() {
  /* reading the content */
  while (Serial.available()) {
    char ch = (char) Serial.read();
    switch(ch){
      case SEP:
        index++;
        break;
      case END_COMM:
        dbConfig->setConfig(parsedMsg[REQ], parsedMsg[LIGHT], parsedMsg[RB]);
        sensors->setReadings(parsedMsg[SOMEONE], parsedMsg[LIGHTSENS]);
        clearMsg();
        break;
      default:
        if(isDigit(ch)){
          parsedMsg[index] += ch;
        }
        break;
    }
  }
}