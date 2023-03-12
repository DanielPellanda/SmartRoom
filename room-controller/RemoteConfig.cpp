#include "RemoteConfig.h"

#define ON "1"

RemoteConfig::RemoteConfig () {
  request = false;
  light = false;
  rollerBlinds = 0;
}

void RemoteConfig::setConfig(String req, String lgt, String rb){
  req == ON ? request = true : request = false;
  lgt == ON ? light = true : light = false;
  rollerBlinds = rb.toInt();
}

bool RemoteConfig::isCtrlReq(){
  return request;
}

bool RemoteConfig::islightOn(){
  return light;
}

int RemoteConfig::getRollerBlindsAngle(){
  return rollerBlinds;
}
