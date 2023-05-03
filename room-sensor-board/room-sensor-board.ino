#include "Arduino.h"
#include "Config.h"
#include "SensorsCheckTask.h"
#include "HttpCommTask.h"

SensorsCheckTask* sensorsTask;
HttpCommTask* commTask;

void setup(){
  Serial.begin(115200);

  sensorsTask = new SensorsCheckTask(LIGHT_PIN, PIR_PIN, LED_PIN);
  commTask = new HttpCommTask();

  sensorsTask->init();
  commTask->init();
  Serial.println("Setup done");
}

void loop(){
  sensorsTask->tick();
  commTask->tick(sensorsTask->isSomeoneInRoom(),
    sensorsTask->getLightLevel());

  delay(BASE_PERIOD);
}
