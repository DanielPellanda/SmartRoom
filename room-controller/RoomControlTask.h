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

  void angle(int angle);
  void lightRules();
  void rollerBlindsRules();
    
  public:
    RoomControlTask(RoomState* currState, int ledPin, int servoPin);

    void init(int period, ClockTask* clockTask, CommunicationTask* commTask);
    int* getRollerBlindsAngle();
    bool* lightsOn();
    void tick();
};

#endif