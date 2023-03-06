#include "LightingTask.h"

LightingTask::LightingTask (RoomState* currState, SensorsReadings* room, int ledPin){
  this->currState = currState;
  this->room = room;
  this->led = new Led(ledPin);
}

void LightingTask::init(int period) {
    Task::init(period);
}

void LightingTask::tick() {
    switch (*this->currState){
    case AUTO:
      if(led->isOff() && room->isSomeoneInRoom() && room->getLightLvl() < DARK){
        led->turnOn();
      } else if (led->isOn()) {
        led->turnOff();
      }
      break;
    case BLUETOOTH:
      break;
    case DASHBOARD:
      break;
    default:
      break;
    }
}