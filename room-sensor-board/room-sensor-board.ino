#include "Arduino.h"
#include "Config.h"
#include "SensorsCheckTask.h"
#include "HttpCommTask.h"

SensorsCheckTask* sensorsTask;
HttpCommTask* commTask;

void setup(){
  Serial.begin(115200);

  sensorsTask = new SensorsCheckTask(LIGHT_PIN, PIR_PIN, LED_PIN);
  commTask = new HttpCommTask(sensorsTask->isSomeoneInRoom(),
   sensorsTask->getLightLevel());

  sensorsTask->init(BASE_PERIOD);
  commTask->init();
}

void loop(){
  sensorsTask->tick();
  commTask->tick();
  delay(BASE_PERIOD);
}
