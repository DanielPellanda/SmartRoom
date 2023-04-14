#include "Scheduler.h"
#include "Arduino.h"
#include "Config.h"
#include "RoomState.h"
#include "ClockTask.h"
#include "RoomControlTask.h"
#include "CommunicationTask.h"

Scheduler taskmgr;
RoomState state;

void setup(){
  Serial.begin(9600);
  state = AUTO;
  ClockTask* clockTask = new ClockTask(&state);
  RoomControlTask* roomControl = new RoomControlTask(&state, LED_PIN, SERVO_PIN);
  CommunicationTask* commTask = new CommunicationTask(&state, BT_RX_PIN, BT_TX_PIN);

  taskmgr.init(BASE_PERIOD);
  clockTask->init(BASE_PERIOD);
  roomControl->init(BASE_PERIOD * 2, clockTask, commTask);
  commTask->init(BASE_PERIOD * 2, clockTask,
    roomControl->getRollerBlindsAngle(), roomControl->lightsOn());

  taskmgr.addTask(clockTask);
  taskmgr.addTask(roomControl);
  taskmgr.addTask(commTask);
}

void loop(){
  taskmgr.schedule();
}
