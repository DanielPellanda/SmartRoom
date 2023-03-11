#ifndef __ROOMCONTROL_TASK__
#define __ROOMCONTROL_TASK__

#include "Task.h"
#include "Led.h"
#include "Servo.h"
#include "ClockTask.h"
#include "CommunicationTask.h"

#define DARK 50
#define ROLLED_UP 0
#define UNROLLED 180
#define WAKEUP 8
#define SLEEP 19
#define DELAY_SERVO 50

class RoomControlTask : public Task {
  RoomState* currState = nullptr;
  Led* led = nullptr;
  Servo* servo = nullptr;
  RemoteConfig* btConfig = nullptr;
  RemoteConfig* dbConfig = nullptr;
  SensorsReadings* sens = nullptr;
  Clock* clock = nullptr;
  int servoPin;
  int* currAngle;
  bool* lights;

  /**
  * applies the given angle 0-180 to the servo motor
  */
  void angle(int angle);
  /**
  * applies the lighting system's rules
  */
  void lightRules();
  /**
  * applies the roller blinds system's rules
  */
  void rollerBlindsRules();
    
  public:
    RoomControlTask(RoomState* currState, int ledPin, int servoPin);
    /**
    * returns a pointer to the current roller blinds unorlling %
    */
    int* getRollerBlindsAngle();
    /**
    * returns a pointer to the room lighting state
    */
    bool* lightsOn();
    void init(int period, ClockTask* clockTask, CommunicationTask* commTask);
    void tick();
};

#endif