#include "MsgServiceSerial.h"

MsgServiceSerial::MsgServiceSerial(SensorsReadings* sens, RemoteConfig* conf){
  this->sensors = sens;
  this->dbConfig = conf;
}

void MsgServiceSerial::sendMsg(String msg){
  Serial.println(msg);
  Serial.flush(); /* Da Arduino 1.0 aspetta che abbia finito di inviare il messaggio */
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
        index = 0;
        break;
      default:
        if(isDigit(ch)){
          parsedMsg[index] += ch;
        }
        break;
    }
  }
}