#ifndef __PIR__
#define __PIR__


#define CALIBRATION_TIME 10

class Pir {
  int pirPin;

  public:
    Pir(int pirPin);
    /**
    * Returns true if a moovement is detected
    */
    bool isMovementDetected();
    /**
    * Calibrates the sensors taking ambient samples
    */
    void calibrate();
};

#endif