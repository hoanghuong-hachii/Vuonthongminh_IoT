#include <SerialCommand.h>
#define SERIAL_DEBUG_BAUD   9600
#include <ArduinoJson.h>
#include <PubSubClient.h>
#include <ESP8266WiFi.h>
#include "ThingsBoard.h"
SerialCommand SCmd;
//Khai báo biến giá trị
short T, H, L, W;
//điều khiển từ web
//#define WIFI_AP "Cap 4.2"
#define WIFI_AP "Opp"
//#define WIFI_PASSWORD "11223344"
#define WIFI_PASSWORD "12341234"
//#define TOKEN "DA"
#define TOKEN "thom"
#define TOKEN1 "huong"
//Bên esp8266
short bomGiot = D2;  //Soil moisture
short bomPhun = D3;  //Humirity
short quat12 = D4;  //Temperature
short den = D5;  //Light
short button = D1;
//Thingsboard
#define GPIO2_PIN 2
#define GPIO3_PIN 3
#define GPIO4_PIN 4
#define GPIO5_PIN 5
#define GPIO6_PIN 6
char thingsboardServer[] = "192.168.43.61";
WiFiClient wifiClient;
// Initialize ThingsBoard instance
PubSubClient client(wifiClient);
short status = WL_IDLE_STATUS;
short stateButton;
// We assume that all GPIOs are LOW
boolean gpioState[] = {false, false, false, false, false};
boolean gpioState1[] = {false, false, false, false};
void setup() {
  Serial.begin(SERIAL_DEBUG_BAUD);
  SCmd.addCommand("T", RelayTB_val);    //Nhận lệnh
  //Dữ liệu nhận được sẽ có dạng   T "gt nhiệt độ" "gt độ ẩm" "gt ánh sáng" "gt độ ẩm đất"
  pinMode(button, INPUT_PULLUP);
  pinMode(bomGiot, OUTPUT);
  pinMode(bomPhun, OUTPUT);
  pinMode(quat12, OUTPUT);
  pinMode(den, OUTPUT);
  InitWiFi();
  client.setServer( thingsboardServer, 1883 );
  client.setCallback(on_message);
}
void loop() {
  SCmd.readSerial();
  stateButton = digitalRead(button);
  //Serial.println(stateButton);
  //automatic
  client.publish("v1/devices/me/attributes", get_gpio_status1().c_str());
  //control
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  delay(1000);
}
void RelayTB_val() {
  //Nhiệt độ = T
  //Độ ẩm    = H
  //Ánh sáng = L
  //Độ ẩm đất= W
  Serial.println("Đang đọc dữ liệu");
  char *arg;
  //Đọc nhiệt độ
  arg = SCmd.next();
  //Kiểm tra chuỗi nhận được
  if (arg != NULL)
  {
    T = atoi(arg);  // Chuyển đổi ký tự thành số
    Serial.print("First argument was T: ");
    Serial.println(T);
  }
  //Đọc độ ẩm không khí
  arg = SCmd.next();
  if (arg != NULL)
  {
    H = atoi(arg);
    Serial.print("Second argument was H: ");
    Serial.println(H);
  }
  arg = SCmd.next();
  if (arg != NULL)
  {
    L = atoi(arg);
    Serial.print("Third argument was L: ");
    Serial.println(L);
  }
  arg = SCmd.next();
  if (arg != NULL)
  {
    W = atoi(arg);
    Serial.print("Fouth argument was W: ");
    Serial.println(W);
  }
  if (stateButton == 0)
  {
    Serial.println("Chế độ tự động");
    dieuKhien();
  }
  else
  {
    Serial.println("Chế độ manual web");
  }
  delay(100);
}
void dieuKhien() {
  if (T > 40) {
    digitalWrite(quat12, LOW);
    gpioState1[2] = true;
  }
  else {
    digitalWrite(quat12, HIGH);
    gpioState1[2] = false;
  }
  if (H < 30) {
    digitalWrite(bomPhun, LOW);
    gpioState1[1] = true;
  }
  else {
    digitalWrite(bomPhun, HIGH);
    gpioState1[1] = false;
  }
  if (L < 100) {
    digitalWrite(den, LOW);
    gpioState1[3] = true;
  }
  else {
    digitalWrite(den, HIGH);
    gpioState1[3] = false;
  }
  if (W < 60) {
    digitalWrite(bomGiot, LOW);
    gpioState1[0] = true;
  }
  else {
    digitalWrite(bomGiot, HIGH );
    gpioState1[0] = false;
  }
}
// The callback for when a PUBLISH message is received from the server.
void on_message(const char* topic, byte* payload, unsigned int length) {
  Serial.println("On message");
  char json[length + 1];
  strncpy (json, (char*)payload, length);
  json[length] = '\0';
  Serial.print("Topic: ");
  Serial.println(topic);
  Serial.print("Message: ");
  Serial.println(json);
  // Decode JSON request
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& data = jsonBuffer.parseObject((char*)json);
  if (!data.success())
  {
    Serial.println("parseObject() failed");
    return;
  }
  // Check request method
  String methodName = String((const char*)data["method"]);
  if (methodName.equals("getGpioStatus")) {
    // Reply with GPIO status
    String responseTopic = String(topic);
    responseTopic.replace("request", "response");
    client.publish(responseTopic.c_str(), get_gpio_status().c_str());
  } else if (methodName.equals("setGpioStatus")) {
    // Update GPIO status and reply
    set_gpio_status(data["params"]["pin"], data["params"]["enabled"]);
    String responseTopic = String(topic);
    responseTopic.replace("request", "response");
    client.publish(responseTopic.c_str(), get_gpio_status().c_str());
    client.publish("v1/devices/me/attributes", get_gpio_status().c_str());
  }
}
String get_gpio_status() {
  // Prepare gpios JSON payload string
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& data = jsonBuffer.createObject();
  data[String(GPIO2_PIN)] = gpioState[0] ? true : false;
  data[String(GPIO3_PIN)] = gpioState[1] ? true : false;
  data[String(GPIO4_PIN)] = gpioState[2] ? true : false;
  data[String(GPIO5_PIN)] = gpioState[3] ? true : false;
  data[String(GPIO6_PIN)] = gpioState[4] ? true : false;
  char payload[256];
  data.printTo(payload, sizeof(payload));
  String strPayload = String(payload);
  Serial.print("Get gpio status: ");
  Serial.println(strPayload);
  return strPayload;
}
String get_gpio_status1() {
  // Prepare gpios JSON payload string
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& data = jsonBuffer.createObject();
  data[String("7")] = gpioState1[0] ? true : false;
  data[String("8")] = gpioState1[1] ? true : false;
  data[String("9")] = gpioState1[2] ? true : false;
  data[String("10")] = gpioState1[3] ? true : false;
  char payload[256];
  data.printTo(payload, sizeof(payload));
  String strPayload = String(payload);
//  Serial.print("Get gpio status: ");
//  Serial.println(strPayload);
  return strPayload;
}
void set_gpio_status(int pin, boolean enabled) {
  if (pin == GPIO2_PIN) {
    // Output GPIOs state
    digitalWrite(bomGiot, enabled ? LOW : HIGH);
    // Update GPIOs state
    gpioState[0] = enabled;
  } else if (pin == GPIO3_PIN) {
    // Output GPIOs state
    digitalWrite(bomPhun, enabled ? LOW : HIGH);
    // Update GPIOs state
    gpioState[1] = enabled;
  } else if (pin == GPIO4_PIN) {
    // Output GPIOs state
    digitalWrite(quat12, enabled ? LOW : HIGH);
    // Update GPIOs state
    gpioState[2] = enabled;
  } else if (pin == GPIO5_PIN) {
    // Output GPIOs state
    digitalWrite(den, enabled ? LOW : HIGH);
    // Update GPIOs state
    gpioState[3] = enabled;
  } else if (pin == GPIO6_PIN) {
    // Output GPIOs state
    digitalWrite(den, enabled ? HIGH : HIGH);
    digitalWrite(quat12, enabled ? HIGH : HIGH);
    digitalWrite(bomGiot, enabled ? HIGH : HIGH);
    digitalWrite(bomPhun, enabled ? HIGH : HIGH);
    // Update GPIOs state
    gpioState[4] = enabled;
  }
}
void InitWiFi() {
  Serial.println("Connecting to AP ...");
  // attempt to connect to WiFi network
  WiFi.begin(WIFI_AP, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connected to AP");
}
void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    status = WiFi.status();
    if ( status != WL_CONNECTED) {
      WiFi.begin(WIFI_AP, WIFI_PASSWORD);
      while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
      }
      Serial.println("Connected to AP");
    }
    Serial.print("Connecting to Thingsboard node ...");
    // Attempt to connect (clientId, username, password)
    if ( client.connect("ESP8266 Device", TOKEN, NULL) ) {
      Serial.println( "[DONE]" );
      // Subscribing to receive RPC requests
      client.subscribe("v1/devices/me/rpc/request/+");
      // Sending current GPIO status
      Serial.println("Sending current GPIO status ...");
      client.publish("v1/devices/me/attributes", get_gpio_status().c_str());
    } else {
      Serial.print( "[FAILED] [ rc = " );
      Serial.print( client.state() );
      Serial.println( " : retrying in 5 seconds]" );
      // Wait 5 seconds before retrying
      delay( 5000 );
    }
  }
}
