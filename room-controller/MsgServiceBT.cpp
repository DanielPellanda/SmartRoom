#include "MsgServiceBT.h"

MsgServiceBT::MsgServiceBT(int rxPin, int txPin, RemoteConfig* conf){
  channel = new SoftwareSerial(rxPin, txPin);
  btConfig = conf;
}

void MsgServiceBT::init(){
  channel->begin(9600);
  failedComm = 0;
  clearMsg();
}

void MsgServiceBT::sendMsg(String msg){
  channel->println(msg);
}

void MsgServiceBT::receiveMsg(){
  int i = 0;
  !channel->available() ? failedComm++ : failedComm = 0;
  if (failedComm == TIMEOUT) {
    btConfig->setConfig("0","0","0");
  }
  while (channel->available()) {
    char ch = (char) channel->read();
    switch(ch){
      case SEP:
        i++;
        break;
      case END_COMM:
        btConfig->setConfig(parsedMsg[REQ], parsedMsg[LIGHT], parsedMsg[RB]);
        clearMsg();
        break;
      default:
        parsedMsg[i] += ch;
        break;
    }
  }
}
