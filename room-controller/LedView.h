#ifndef __LED_VIEW__
#define __LED_VIEW__

typedef enum{OUT, IN} Mode;

class LedView{
  public:
  /**
  * Turns on/off  the led assigned at the given pin
  */
  static void setLedState(int pin, LedState state) {
    if (state == ON) {
      digitalWrite(pin, HIGH);
      return;
    }
      digitalWrite(pin, LOW);
    }
  }
  /**
  * Sets up the given pin to either I or O
  */
  static void setupPin(int pin, Mode mode) {
    if (mode == IN) {
      pinMode(pin, INPUT);
    }
    if (mode == OUT) {
      pinMode(pin, OUTPUT);
    }
  }
};

#endif