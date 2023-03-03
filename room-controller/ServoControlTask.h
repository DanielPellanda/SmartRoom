#ifndef __SERVO_TASK__
#define __SERVO_TASK__

#include "Servo.h"
#include "Task.h"

#define BLINDS_MIN 0
#define BLINDS_MAX 180

#define DELAY_SERVO 50

typedef enum {AUTO, REMOTE} ServoControl;

class ServoControlTask : public Task {
  Servo* servoM = nullptr;
  ServoControl currControl;
  String mode = "AUTO";
  int remoteAngle = 0;
  int currAngle = 0;
  int pin;

  /**
  * applies the given angle on the servo motor
  */
  void angle(int angle) {
    if (angle < VALVE_MIN || angle > VALVE_MAX) {
      return;
    }
    currAngle = angle;

    float coeff = (2250.0-750.0)/180;
    servoM->attach(pin);
    servoM->write(750 + angle*coeff);
    delay(DELAY_SERVO);
    servoM->detach();
    BaseView::printLog("Valve angle set to " + String(currAngle) + " degrees");
  }

  /**
  * calculates the opening angle of the valve from the JavaApp data
  */
  int angleFromPercentage(int percentage) {
    return map(percentage,0,100,BLINDS_MIN,BLINDS_MAX);
  }

    public:
      ServoControlTask(AlarmState* currState, int pin);
      void init(int period, SonarCheckTask* sonar, Button* inputBtn, Potentiometer* pot);
      /**
      * returns the current valve opening percentage
      */
      int getCurrValveOpening();
      /**
      * returns a string describing the current valve control
      */
      String getValveControl();
      /**
      * sets the valve control mode
      */
      void setValveControl(ServoControl crtl);
      /**
      * sets the remote angle to support remote control
      */
      void setRemoteAngle(int angle);
      void tick();
};

#endif