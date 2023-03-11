#ifndef __ROOMCONTROL_TASK__
#define __ROOMCONTROL_TASK__

#include "Task.h"
#include "Led.h"
#include "Servo.h"
#include "ClockTask.h"
#include "CommunicationTask.h"


#define DARK 50
#define VALVE_MIN 0
#define VALVE_MAX 180
#define DELAY_SERVO 50

class RoomControlTask : public Task {
  RoomState* currState = nullptr;
  Led* led = nullptr;
  Servo* servo = nullptr;
  RemoteConfig* btConfig = nullptr;
  RemoteConfig* dbConfig = nullptr;
  SensorsReadings* sens = nullptr;
  Clock* clock = nullptr;
  int servoPin, currAngle;

  void angle(int angle);
    
  public:
    RoomControlTask(RoomState* currState, int ledPin, int servoPin);

    void init(int period, ClockTask* clockTask, CommunicationTask* commTask);
    bool isLightOn();
    int getRollerBlindsAngle();
    void tick();
};

#endif