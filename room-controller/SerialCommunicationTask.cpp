#include "SerialCommunicationTask.h"

void SerialCommunicationTask::init(int period) {
    Task::init(period);
    MsgService.init();
}

void SerialCommunicationTask::tick() {
  MsgService.sendMsg();
    
  if(MsgService.isMsgAvailable()){
    Msg* msg = MsgService.receiveMsg();
    String info = msg->getContent();
    if(info.equals()) {
      
    }
    delete msg;
  }
}
