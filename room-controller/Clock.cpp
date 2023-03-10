#include "Clock.h"

Clock::Clock (int time){
  this->hours = time;
  this->minutes = time;
}

int Clock::getHour() {
  return this->hours;
}

int Clock::getMinute() {
  return this->minutes;
}

void Clock::clockTick() {
  this->minutes = (this->minutes + SKIP) % MINUTES;
  if(this->minutes == 0){
    this->hours = (this->hours + 1) % HRS_FORMAT;
  }
}
