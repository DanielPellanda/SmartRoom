#include "MsgServiceBT.h"

#define MAX_LENGTH 3

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
  int nchread = 0, i = 0;
  !channel->available() ? failedComm++ : failedComm = 0;
  if (failedComm == TIMEOUT) {
    btConfig->setConfig("0","0","100");
  }
  while (channel->available() && nchread <= MAX_LENGTH) {
    char ch = (char) channel->read();
    switch(ch){
      case SEP:
        nchread = 0;
        i++;
        break;
      case END_COMM:
        btConfig->setConfig(parsedMsg[REQ], parsedMsg[LIGHT], parsedMsg[RB]);
        channel->flush();
        break;
      default:
        nchread ++;
        parsedMsg[i] += ch;
        break;
    }
  }
  clearMsg();
}
