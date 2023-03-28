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
  this->minutes += SKIP;
  if(this->minutes >= HOUR){
    this->hours = (this->hours + 1) % HRS_FORMAT;
    this->minutes =  this->minutes % HOUR;
  }
}
