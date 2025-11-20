import configparser

from app.kafka_broker.common_kafka import setup_consumer, setup_producer, send_message

# Config 파일 로드
config = configparser.ConfigParser()
config.read('./app/config/config.ini')

# KAFKA 섹션에서 기본 변수 로드
KAFKA_HOST = config['KAFKA']['KAFKA_HOST']
KAFKA_PORT = config['KAFKA']['KAFKA_PORT']
KAFKA_REQUEST_TOPIC = config['KAFKA']['KAFKA_REQUEST_TOPIC']
KAFKA_RESPONSE_TOPIC = config['KAFKA']['KAFKA_RESPONSE_TOPIC']
KAFKA_GROUP_ID = config['KAFKA']['KAFKA_GROUP_ID']

kafka_consumer_msg_ex = {
"serviceId":  "aislOpt",
"sensorParam": [
    {
        "Sensor1": {
            "range": 5,
            "profile": "A"
            }
        },
        {
        "Sensor2": {
            "range": 5,
            "profile": "A"
            }
        },
        {
        "Sensor3": {
            "range": 5,
            "profile": "A"
            }
        }
    ],
"filterParam": {
    "binarization": {
        "threshold": 145
    },
    "normalization": {
        "mean": 156,
        "std": 0.65
        }
    },
"hyperParam": {
    "resnet": {
        "residual_block": 3,
        "learning_rate": 0.001,
        "epoch": 10
    }
},
"optIter": 1,
"sensorType": "radar"
}


# how to get sensor list from kafka_consumer_msg_ex
sensor_list = []
sensor_info_list = kafka_consumer_msg_ex['sensorParam']
for sensor_info in sensor_info_list:
    sensor_list.append((lambda sensor_info: list(sensor_info.keys())[0])(sensor_info))
print(sensor_list)


# how to make a subscription nu string and iotplatform dir variable from config.ini
# service_id = os.environ['SERVICE_ID']
# hard coded service_id for now
service_id = 'aislOptimization'

# sub_nu_list = []
# for sensor in sensor_list:
#     sub_nu_list.append(config['API']['DEFAULT_SUBSCRIPTION_NU'].format(service_id+sensor))
# print(sub_nu_list)

producer = setup_producer(KAFKA_HOST, KAFKA_PORT)
msg = send_message(producer, KAFKA_REQUEST_TOPIC, 'service_demo2', kafka_consumer_msg_ex)

print(kafka_consumer_msg_ex)