#include "MsgServiceBT.h"

MsgServiceBT::MsgServiceBT(int rxPin, int txPin, RemoteConfig* conf){
  channel = new SoftwareSerial(rxPin, txPin);
  btConfig = conf;
}

void MsgServiceBT::init(){
  channel->begin(9600);
  clearMsg();
}

bool MsgServiceBT::sendMsg(String msg){
  if(channel->available()){
    channel->println(msg);
    return true;
  }
  return false;
}

void MsgServiceBT::receiveMsg(){
  int i = 0;
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
