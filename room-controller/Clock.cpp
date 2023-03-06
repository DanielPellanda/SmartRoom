#include "Clock.h"

Clock::Clock (int time){
  this->timeBand = time;
}

int Clock::getTime() {
  return this->timeBand;
}

int Clock::nextTimeBand() {
  this->timeBand = (this->timeBand + 1) % HRS_FORMAT;
  return this->timeBand;
}
