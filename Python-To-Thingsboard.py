import serial
import time
import schedule
import paho.mqtt.client as mqttclient
import time
import json
import mysql.connector #mô đun hỗ trợ kết nối tới mySql
from datetime import datetime
BROKER_ADDRESS = "192.168.22.18"
PORT = 1883
THINGS_BOARD_ACCESS_TOKEN = "huong"

def main_func():
    arduino = serial.Serial('com6', 9600)
    print('Established serial connection to Arduino')
    arduino_data = arduino.readline()
    decoded_values = str(arduino_data[0:len(arduino_data)].decode("utf-8"))
    list_values = decoded_values.split('x')
    for item in list_values:
        list_in_floats.append(float(item))
    print(f'Collected readings from Arduino: {list_in_floats}')
    tempt = 0
    humit = 0
    lightt = 0
    soilt = 0
    for item in list_in_floats:
        # print(list_in_floats[0])
        tempt = list_in_floats[0]
        global temp 
        temp = tempt
        # print("ok", temp)
        # print(type(item))
        # print(list_in_floats[1])
        humit = list_in_floats[1]
        global humi 
        humi = humit
        # print("ok", humi)
        # print(list_in_floats[2])
        lightt = list_in_floats[2]
        global light
        light = lightt
        # print("ok", light)
        # print(list_in_floats[3])
        soilt = list_in_floats[3]
        global soil 
        soil = soilt
        # print("ok", soil)
    arduino_data = 0
    list_in_floats.clear()
    list_values.clear()
    # arduino.close()
    print('Connection closed')
    print('<----------------------------->')

def subscribed(client, userdata, mid, granted_qos):
    print("Subscribed...")
# def recv_message(client, userdata, message):
#     print("Received: ", message.payload.decode("utf-8"))
#     temp_data = {'value': True}
#     try:
#         #json.loads()-> deserialize một json string, dùng hàm loads.
#         jsonobj = json.loads(message.payload)
#         if jsonobj['method'] == "setValue":
#             temp_data['value'] = jsonobj['params']
#             client.publish('v1/devices/me/attributes', json.dumps(temp_data), 1)
#     except:
#         pass
def connected(client, usedata, flags, rc):
    if rc == 0:
        print("Thingsboard connected successfully!!")
        if(client.subscribe("v1/devices/me/rpc/request/+")== True):{
            print("Connection ok")
        }
        else:
            print("Falseeeeeee")
    else:
        print("Connection is failed")

# ----------------------------------------Main Code------------------------------------
# Declare variables to be used
list_values = []
list_in_floats = []
temp = 0
humi = 0
light = 0
soil = 0
print('Program started')
# Setting up the Arduino
schedule.every(10).seconds.do(main_func)
#---------thingsboard---------- "Gateway_Thingsboard"
#client.on_connect = connected
client = mqttclient.Client()
client.username_pw_set(THINGS_BOARD_ACCESS_TOKEN)
client.connect(BROKER_ADDRESS, 1883)
client.loop_start()
#client.on_subscribe = subscribed
# client.on_message = recv_message

# #cursor.execute("select * from sensor;")
# data = cursor.fetchall()  #đọc kết quả truy vấn
# conn.commit()
# print(conn)
# conn.close()
while True:
    #Json là một chuẩn định dạng file được sử dụng phổ biến để lưu trữ và trao đổi thông tin
    #Serialize là quá trình chuyển một object trong chương trình về dạng mà máy tính có thể lưu trữ hoặc truyền đi được. 
    #Trong python, để serialize dữ liệu, ta dùng json.dump() hoặc json.dumps()
    #Deserialize là quá trình khôi phục lại các object từ file json hoặc chuỗi các bit nhận được từ mạng. 
    #Trong python, để deserialize dữ liệu, ta dùng json.load() hoặc json.loads()
    #Với những kiểu dữ liệu mà module json của python không hỗ trợ, ta cung cấp thêm các hàm trợ giúp cho python.
    #-------Arduino to Python--------
    schedule.run_pending()
    time.sleep(1)
    #------Python to Thingsboard-----
    collect_data = {'nhietdo': temp, 'doamKK': humi, 'anhsang': light, 'doamDat': soil}
    client.publish('v1/devices/me/telemetry', json.dumps(collect_data)) 
    #collect_data = {'nhietdo': 10, 'doamKK': 100, 'anhsang': 101, 'doamDat': 90}
    #json.dumps() => lưu dữ liệu dưới dạng file json
    #Gửi dữ liệu lên phpMyadmin
    T = str(temp)
    H = str(humi)
    L = str(light)
    W = str(soil)
    conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="",
    database="users_database")
    cursor = conn.cursor()
    T = str(temp)
    H = str(humi)
    L = str(light)
    W = str(soil)
    now = datetime.now()
    formatted_datetime = now.strftime('%Y-%m-%d %H:%M:%S')
    sql = "INSERT INTO data_table (nhietdo, doamkk, doamdat, anhsang, date, access) VALUES (%s, %s, %s, %s, %s, %s)"
    val = (T, H, W, L, formatted_datetime, "huong")
    cursor.execute(sql, val)
    data = cursor.fetchall()  #đọc kết quả truy vấn
    conn.commit()
    print(conn)
    conn.close()
    time.sleep(1)

