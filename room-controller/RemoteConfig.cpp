#include "RemoteConfig.h"

#define ON 1
#define OFF 0

RemoteConfig::RemoteConfig () {
  request = 0;
  light = false;
  rollerBlinds = 0;
}

void RemoteConfig::setConfig(String req, String lgt, String rb){
  request = req.toInt();
  lgt.toInt() == ON ? light = true : light = false;
  rollerBlinds = rb.toInt();
}

bool RemoteConfig::isCtrlReq(){
  return request == ON;
}

bool RemoteConfig::isReleaseReq(){
  return request == OFF;
}

bool RemoteConfig::islightOn(){
  return light;
}

int RemoteConfig::getRollerBlindsAngle(){
  return rollerBlinds;
}
