//A4-SDA
//A5-SCL
#include <LiquidCrystal_I2C.h>
#include <SoftwareSerial.h>
#include "DHT.h"
const short DHTPIN = 4;
const short DHTTYPE = DHT11;
short speaker = 8;
DHT dht(DHTPIN, DHTTYPE);
#define Tx 2             //Định nghĩa chân 2 là Tx
#define Rx 3             //Định nghĩa chân 3 là Rx 
LiquidCrystal_I2C lcd(0x27, 16, 2);
SoftwareSerial mySerial(Rx, Tx); //Khởi tạo cổng serial mềm
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  mySerial.begin(9600);
  pinMode(speaker, OUTPUT);
  //LCD
  lcd.init();
  lcd.backlight();
  lcd.begin(16, 2);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("** WELCOME TO **");
  lcd.setCursor(0, 1);
  lcd.print("----FARMING!----");
  dht.begin();
  delay(1000);
}
void loop() {
  lcd.clear();
  //++++++++CÁCH 1:
  //DHT11 Nhiệt độ-độ ẩm không khí
  short h = dht.readHumidity();
  short t = dht.readTemperature();
  //Ánh sáng
  short quangTro = analogRead(A0);
  //Độ ẩm đất
  short gt = analogRead(A1);
  short doAmFake = map(gt, 0, 1023, 0, 100);
  short doAmReal = 100 - doAmFake;
  //===============Hiển thị lên LCD
  //--------------1. Giá trị nhiệt độ, độ ẩm
  lcd.setCursor(0, 0);
  lcd.print("T= ");
  lcd.print(t);
  lcd.print(" oC ");
  lcd.print("H= ");
  lcd.print(h);
  lcd.print("%");
  //--------------2. Giá trị ánh sáng
  lcd.setCursor(0, 1);
  lcd.print("L= ");
  lcd.print(quangTro);
  //--------------3. Giá trị độ ẩm đất
  lcd.print("    W= ");
  lcd.print(doAmReal);
  lcd.print("%");
  //In lên Serial để chuyển lên Thingsboard
  Serial.print(t);
  Serial.print("x");
  Serial.print(h);
  Serial.print("x");
  Serial.print(quangTro);
  Serial.print("x");
  Serial.println(doAmReal);
  //==========Gửi giá trị ra cổng Software Serial
  mySerial.print("T ");
  mySerial.print(t);
  mySerial.print(" ");
  mySerial.print(h);
  mySerial.print(" ");
  mySerial.print(quangTro);
  mySerial.print(" ");
  mySerial.print(doAmReal);
  mySerial.print("\r\n");
  //Kiểm tra khi hệ thống tự động bị hỏng
  if(t>50 || h <25 || quangTro <50 || doAmReal <45){
    digitalWrite(speaker, HIGH);
  }
  else{
    digitalWrite(speaker, LOW);
  }
  delay(800);
}
