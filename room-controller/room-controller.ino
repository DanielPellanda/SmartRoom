#include "Scheduler.h"
#include "Arduino.h"
#include "Config.h"
#include "RoomState.h"

Scheduler taskmgr;
RoomState* state;

void setup(){
  Serial.begin(9600);
}

void loop(){
  taskmgr.schedule();
}
