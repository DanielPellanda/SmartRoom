#ifndef __REMOTE_CONFIG__
#define __REMOTE_CONFIG__

#define ON "1"

class RemoteConfig {
  bool request;
  bool light;
  int rollerBlinds;

public:
  RemoteConfig () {
    request = false;
    light = false;
    rollerBlinds = 0;
  }

  void setConfig(String req, String lgt, String rb){
    req == ON ? request = true : request = false;
    lgt == ON ? light = true : light = false;
    rollerBlinds = rb.toInt();
  }

  bool isCtrlReq(){
    return request;
  }

  bool islightOn(){
    return light;
  }

  int getRollerBlindsAngle(){
    return rollerBlinds;
  }
};

#endif