#ifndef __CLOCK__
#define __CLOCK__

#define HRS_FORMAT 24
#define HOUR 60
#define SKIP 5

class Clock {
  int hours;
  int minutes;
    
  public:
    Clock(int time);
    /**
    * get the clock's hour
    */
    int getHour();
    /**
    * get the clock's minute
    */
    int getMinute();
    /**
    * puts the clock on the next time tick
    */
    void clockTick();
    
};

#endif
