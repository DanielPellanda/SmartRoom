#include "ServoControlTask.h"
#include "SerialCommunicationTask.h"
#include "Scheduler.h"
#include "Arduino.h"
#include "Config.h"

Scheduler taskmgr;

void setup(){
  Serial.begin(9600);

  int led = LED;

  ServoControlTask* servoTask = new ServoControlTask(&state, SERVO_PIN);
  SerialCommunicationTask* serialComm = new SerialCommunicationTask(&state);

  taskmgr.init(BASE_PERIOD);
  servoTask->init(3 * BASE_PERIOD, sonarTask, button, pot);
  ledTask->init(BASE_PERIOD, lightTask, pirTask);
  serialComm->init(4 * BASE_PERIOD, sonarTask, servoTask, lightTask, ledTask);

  taskmgr.addTask(serialComm);
  taskmgr.addTask(servoTask);
  taskmgr.addTask(ledTask);
}

void loop(){
  #ifdef DEBUG
    printAlarmState();
  #endif
  taskmgr.schedule();
}
