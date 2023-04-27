#include "RoomControlTask.h"

RoomControlTask::RoomControlTask (RoomState* currState, int ledPin, int servoPin){
  this->currState = currState;
  this->led = new Led(ledPin);
  this->servo = new Servo();
  this->servoPin = servoPin;
  *this->currAngle = UNROLLED;
  *this->lights = false;
}

void RoomControlTask::init(int period, ClockTask* clockTask, CommunicationTask* commTask) {
  this->btConfig = commTask->getBTConfig();
  this->dbConfig = commTask->getDBConfig();
  this->sens = commTask->getSensorsReadings();
  this->clock = clockTask->getClock();
  Task::init(period);
}

void RoomControlTask::angle(int angle) {
  if (angle < ROLLED_UP || angle > UNROLLED) {
    this->angle(UNROLLED);
    return;
  }
  *currAngle = angle;

  float coeff = (2250.0-750.0)/180;
  servo->attach(servoPin);
  servo->write(750 + angle*coeff*1.8);
  delay(DELAY_SERVO);
  servo->detach();
}

void RoomControlTask:: lightRules(){
  if(!sens->isSomeoneInRoom()){
    if (led->isOn()){
      led->turnOff();
    }
  } else {
    if(sens->getLightLvl() <= DARK && led->isOff()){
      led->turnOn();
    }
  }
}

void RoomControlTask:: rollerBlindsRules(){
  if(clock->getHour() >= SLEEP || clock->getHour() < WAKEUP){
    if(*currAngle != UNROLLED && !sens->isSomeoneInRoom()){
      angle(UNROLLED);
    }
  } else{
    if(*currAngle == UNROLLED && sens->isSomeoneInRoom()){
      Serial.println(*currAngle);
      angle(ROLLED_UP);
    }
  }
}

void RoomControlTask::tick() {
  switch (*currState){
    case AUTO:
      lightRules();
      rollerBlindsRules();
      break;
    case BLUETOOTH:
      angle(btConfig->getRollerBlindsAngle());
      btConfig->islightOn() ? led->turnOn() : led->turnOff();
      break;
    case DASHBOARD:
      angle(dbConfig->getRollerBlindsAngle());
      dbConfig->islightOn() ? led->turnOn() : led->turnOff();
      break;
    default:
      break;
  }
  led->isOn() ? *lights = true : *lights = false;
}

int* RoomControlTask::getRollerBlindsAngle(){
  return this->currAngle;
}

bool* RoomControlTask::lightsOn(){
  return this->lights;
}