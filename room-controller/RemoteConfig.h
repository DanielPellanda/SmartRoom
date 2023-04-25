#ifndef __REMOTE_CONFIG__
#define __REMOTE_CONFIG__

#include "Arduino.h"

class RemoteConfig {
  long request;
  bool light;
  long rollerBlinds;

  public:
    RemoteConfig ();
    /**
    * interprets the input strings taken from serial line and assigns them to internal fields
    */
    void setConfig(String req, String lgt, String rb);
    /**
    * true if is requested remote control
    */
    bool isCtrlReq();
    /**
    * true if is released remote control
    */
    bool isReleaseReq();
    /**
    * true if lights are set to be on
    */
    bool islightOn();
    /**
    * returns the current unrolling percentage of the blinds
    */
    int getRollerBlindsAngle();
};

#endif