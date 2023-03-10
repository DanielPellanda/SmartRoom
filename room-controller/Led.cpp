#include "Led.h"

Led::Led(int pin){
  this->pin = pin;
  this->state = OFF;
  pinMode(pin, OUTPUT);
}

bool Led::isOn() {
  return state == ON;
}

bool Led::isOff() {
  return state == OFF;
}

int Led::getPin() {
  return pin;
}

void Led::turnOn() {
  state = ON;
  digitalWrite(pin, HIGH);
}

void Led::turnOff() {
  state = OFF;
  digitalWrite(pin, LOW);
}
