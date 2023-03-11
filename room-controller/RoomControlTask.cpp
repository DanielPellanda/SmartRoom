#include "RoomControlTask.h"

RoomControlTask::RoomControlTask (RoomState* currState, int ledPin, int servoPin){
  this->currState = currState;
  this->led = new Led(ledPin);
  this->servo = new Servo();
  this->servoPin = servoPin;
}

void RoomControlTask::angle(int angle) {
  if (angle < VALVE_MIN || angle > VALVE_MAX) {
    return;
  }
  currAngle = angle;

  float coeff = (2250.0-750.0)/180;
  servo->attach(servoPin);
  servo->write(750 + angle*coeff);
  delay(DELAY_SERVO);
  servo->detach();
}

void RoomControlTask::init(int period, ClockTask* clockTask, CommunicationTask* commTask) {
  Task::init(period);
  this->btConfig = commTask->getBTConfig();
  this->dbConfig = commTask->getDBConfig();
  this->sens = commTask->getSensorsReadings();
  this->clock = clockTask->getClock();
  angle(VALVE_MIN);
}

void RoomControlTask::tick() {
    switch (*currState){
    case AUTO:
      if(led->isOff()){
        led->turnOn();
      } else if (led->isOn()) {
        led->turnOff();
      }
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
}