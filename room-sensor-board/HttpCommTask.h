#ifndef __WIFI_TASK__
#define __WIFI_TASK__

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

class HttpCommTask {
  const char* ssid = "EOLO - FRITZ!Box 7430 GC";
  const char* password = "29721512722134519707";
  const char* serverURL = "http://192.168.178.119:9000";
  bool* someone;
  int* light;

  void connectToWifi();
  int sendMsg(String msg);

	public:
		HttpCommTask(bool* someone, int* light);
    void init();
    void tick();
};

#endif