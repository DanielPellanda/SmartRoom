#ifndef __LED__
#define __LED__

#include "Arduino.h"
typedef enum {ON, OFF} LedState;

class Led {
  int pin;
  LedState state;
    
  public:
    Led(int pin);
    /**
    * returns true if the led is on
    */
    bool isOn();
    /**
    * returns true if the led is off
    */
    bool isOff();
    /**
    * returns the pin in which is plugged the led
    */
    int getPin();
    /**
    * Turns on the led
    */
    void turnOn();
    /**
    * Turns off the led
    */
    void turnOff();
};

#endif