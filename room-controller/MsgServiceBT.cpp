#include "MsgServiceBT.h"

MsgServiceBT::MsgServiceBT(int rxPin, int txPin, RemoteConfig* conf){
  channel = new SoftwareSerial(rxPin, txPin);
  btConfig = conf;
}

void MsgServiceBT::init(){
  channel->begin(9600);
  failedComm = index = 0;
  clearMsg();
}

void MsgServiceBT::sendMsg(String msg){
  channel->println(msg);
}

void MsgServiceBT::receiveMsg(){
  !channel->available() ? failedComm++ : failedComm = 0;
  if (failedComm == TIMEOUT || index >= NUM_PARAM) {
    btConfig->setConfig("0","0","100");
    clearMsg();
  }
  while (channel->available() > 0 && index < NUM_PARAM) {
    char ch = (char) channel->read();
    switch(ch){
      case SEP:
        index++;
        break;
      case END_COMM:
        btConfig->setConfig(parsedMsg[REQ], parsedMsg[LIGHT], parsedMsg[RB]);
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
