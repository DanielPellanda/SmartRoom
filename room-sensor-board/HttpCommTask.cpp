#include "HttpCommTask.h"

HttpCommTask::HttpCommTask(bool* someone, int* light){
  this-> someone = someone;
  this-> light = light;
}

void HttpCommTask::connectToWifi(){
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}

void HttpCommTask::init() {
  connectToWifi();
}

int HttpCommTask::sendMsg(String msg){
  WiFiClient client;
  HTTPClient http;

  http.begin(client, serverURL);      
  http.addHeader("Content-Type", "text/plain");   
  int retCode = http.POST(msg);   
  http.end();  

  return retCode;
}

void HttpCommTask::tick() {
  String msg;
  int code;

  if (WiFi.status()== WL_CONNECTED){
    // creating message   
    *someone ? msg = "1;" : msg = "0;";
    msg += String(*light);
    // sending message
    code = sendMsg(msg);
    //checking errors
    if (code == 200){
      Serial.println("ok");   
    } else {
      Serial.println(String("error: ") + code);
    }

  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi();
  }
}
