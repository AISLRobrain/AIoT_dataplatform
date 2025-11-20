import json
from urllib.parse import urlparse
from api_util.http_request import http_get, http_post
from api_util.mqtt_request import subscribing_once
from api_util.iot_platform_dto.post_cin_dto import M2MPostCinDTO

def get_iot_platform_cnt_lbl(url):
    cnt_dict = json.loads(http_get(url, iotPlatform = True).text)
    cnt_lbl = cnt_dict['m2m:cnt']["lbl"]

    return cnt_lbl

def get_iot_platform_sub_nu(url):
    sub_dict = http_get(url, iotPlatform = True)
    sub_nu = sub_dict['m2m:sub']["nu"]

    return sub_nu

def sub_iot_platform_cin_con(nu, port): #nu값이 들어감
    print(nu)
    ip = urlparse(nu).hostname
    path_segments = urlparse(nu).path.split('/')
    topic = next(segment for segment in path_segments if segment)
    print(topic)
    port = int(port)

    received_msg = subscribing_once(ip, port, topic)

    parsed_msg = received_msg


    return parsed_msg

def post_iot_platform_sensing_cin(url, sensor_data):
    cin = M2MPostCinDTO(sensor_data)
    response = http_post(url, json=cin.m2m_cin, iotPlatform = True, ty=4)
    return response

def wating_sensor_ready(nu, port):
    while True:
        state = sub_iot_platform_cin_con(nu, port)
        print(state)
        if state == "ready":
            return True
        else:
            continue

def all_cnt_get_uri(path, lbl):
    all_uri = http_get(path, iotPlatform = True)

    for i in range(len(all_uri["m2m:uril"])):
        cnt = http_get(all_uri["m2m:uril"][i])
        cnt_lbl = cnt["m2m:cnt"]["lbl"][0]
        if cnt_lbl == lbl:
            cnt_name = cnt["m2m:cnt"]["rn"]
            return cnt_name
        else:
            continue
    
    return "none_exist"

def all_cin_get_uri(path, max_retries=10):
    path = path + '?fu=1&ty=4'
    parsed_path = urlparse(path)
    base_path = f"{parsed_path.scheme}://{parsed_path.netloc}/"
    con_list = []
    all_uri = http_get(path, iotPlatform=True)
    # print(all_uri)

    for i in range(len(all_uri["m2m:uril"])):
        retries = 0
        while retries < max_retries:
            cin = http_get(base_path + all_uri["m2m:uril"][i], iotPlatform=True)
            
            if cin is not None:
                con = cin["m2m:cin"]["con"]
                con_list.append(con)
                break  # 성공적으로 데이터를 가져왔으므로 다음 항목으로 이동
            else:
                retries += 1
                print(f"Retry {retries} for URL: {base_path + all_uri['m2m:uril'][i]}")

        if retries == max_retries:
            print(f"Failed to get data after {max_retries} attempts for URL: {base_path + all_uri['m2m:uril'][i]}")

    return con_list



def all_cin_count(path):
    all_uri = http_get(path+'?fu=1&ty=4', iotPlatform = True)
    cin_count = len(all_uri["m2m:uril"])
    return cin_count

if __name__ == "__main__":
    # data = all_cnt_get_uri("Mobius/ISP/target?fu=1&ty=3", {'np':49})
    data = all_cin_get_uri('http://203.250.148.120:20519/Mobius/service302/Sensor2/state')
    # data = get_uri('Mobius/RoadDetection/radar/train/radar1?fu=1&ty=3&lvl=1')['m2m:uril']
    print(data)
    data2 = all_cin_count('http://203.250.148.120:20519/Mobius/service302/Sensor2/state')
    print(data2)