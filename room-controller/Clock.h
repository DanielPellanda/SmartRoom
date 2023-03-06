#ifndef __CLOCK__
#define __CLOCK__

#define HRS_FORMAT 24

class Clock {
  int timeBand;
    
  public:
    Clock(int time);
    /**
    * returns time in hours
    */
    int getTime();
    /**
    * sets the clock on the next time band and returns the new value
    */
    int nextTimeBand();
    
};

#endif