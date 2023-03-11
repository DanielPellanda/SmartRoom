#ifndef __REMOTE_CONFIG__
#define __REMOTE_CONFIG__

#include "Arduino.h"

class RemoteConfig {
  bool request;
  bool light;
  int rollerBlinds;

public:
  RemoteConfig ();

  void setConfig(String req, String lgt, String rb);
  bool isCtrlReq();
  bool islightOn();
  int getRollerBlindsAngle();
};

#endif