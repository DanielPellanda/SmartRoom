#ifndef __WIFI_TASK__
#define __WIFI_TASK__

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

class HttpCommTask {
  const char* ssid = "EOLO - FRITZ!Box 7430 GC";
  const char* password = "29721512722134519707";
  const char* serverURL = "http://192.168.178.119:5067/updateData";
  /**
  * connects to wifi
  */
  void connectToWifi();
  /**
  * sends msg using http
  */
  int sendMsg(String msg);

	public:
    void init();
    void tick(bool someone, int light);
};

#endif