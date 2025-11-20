import configparser
import os
import logging
import numpy as np
import multiprocessing
import uuid
import time

from kafka_broker.common_kafka import setup_consumer, setup_producer, send_message
from api_util.iot_platform_api_util import get_iot_platform_sub_nu, post_iot_platform_sensing_cin, wating_sensor_ready, all_cin_get_uri
from api_util.data_hub_api_util import post_data_hub_sensing_cin

# Config 파일 로드
config = configparser.ConfigParser()
config.read('./config/config.ini')

# COMMON 섹션에서 기본 변수 로드
ENGINE_TYPE = config['COMMON']['ENGINE_TYPE']

# KAFKA 섹션에서 기본 변수 로드
KAFKA_HOST = config['KAFKA']['KAFKA_HOST']
KAFKA_PORT = config['KAFKA']['KAFKA_PORT']
KAFKA_REQUEST_TOPIC = config['KAFKA']['KAFKA_REQUEST_TOPIC']
KAFKA_RESPONSE_TOPIC = config['KAFKA']['KAFKA_RESPONSE_TOPIC']
KAFKA_GROUP_ID = config['KAFKA']['KAFKA_GROUP_ID']

# API 섹션에서 기본 변수 로드
IOTTPLATFORM_URL_HOST = config['API']['IOTTPLATFORM_URL_HOST']
IOTPLATFORM_HTTP_PORT = config['API']['IOTPLATFORM_HTTP_PORT']
IOTPLATFORM_MQTT_PORT = config['API']['IOTPLATFORM_MQTT_PORT']
IOTPLATFORM_URL_HTTP = config['API']['IOTPLATFORM_URL_HTTP']
IOTPLATFORM_URL_MQTT = config['API']['IOTPLATFORM_URL_MQTT']
DATAHUB_URL_HOST_INGEST_ENDPOINT = config['API']['DATAHUB_URL_HOST_INGEST_ENDPOINT']

# 임의로 SERVICE_ID 설정 (추후 환경 셋업 후 삭제 및 아래 주석 해지 요망)
SERVICE_ID = os.environ['SERVICE_ID']
SENSOR_TYPE = os.environ['SENSOR_TYPE']

logger = logging.getLogger(SERVICE_ID + "\'s" + ENGINE_TYPE +"Logger")
handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.INFO)

logger.info(f"Service ID: {SERVICE_ID}")

KAFKA_GROUP_ID = SERVICE_ID + '_' + ENGINE_TYPE

# Consumer 및 Producer 설정
consumer = setup_consumer(KAFKA_HOST, KAFKA_PORT, KAFKA_REQUEST_TOPIC, KAFKA_GROUP_ID)
producer = setup_producer(KAFKA_HOST, KAFKA_PORT)


for message in consumer:
    if message.key == SERVICE_ID:
        try:
            message_content = message.value['content']
            logger.info(f"message_content: {message_content}")
            sensor_list = list(message_content['sensorParam'].keys())
            logger.info(f"sensor_list: {sensor_list}")
            state_paths = message_content['dataResourceInformation']['ioTPlatformInformation']['statePath']
            target_paths = message_content['dataResourceInformation']['ioTPlatformInformation']['targetPath']
            train_paths = message_content['dataResourceInformation']['ioTPlatformInformation']['trainDataPath']
            test_paths = message_content['dataResourceInformation']['ioTPlatformInformation']['testDataPath']
            logger.info(f"target_paths: {target_paths}")

            processes = []

            #센서 파라미터 별로 데이터 수집 명령 및 센서 상태 변경
            for SENSOR_ID in sensor_list:
                time.sleep(2)
                logger.info(f"SENSOR_ID: {SENSOR_ID}")

                SENSOR_TARGET_RESOURCE_PATH = 'http://' + IOTTPLATFORM_URL_HOST + ':' + IOTPLATFORM_HTTP_PORT + next((path for path in target_paths if SENSOR_ID in path), None)
                SENSOR_TARGET_CNT_PATH = "/".join(SENSOR_TARGET_RESOURCE_PATH.rsplit("/", 1)[:-1])

                logger.info(f"SENSOR_TARGET_CNT_PATH: {SENSOR_TARGET_CNT_PATH}")
                sensor_ctrl_msg = {
                    "trainDataPath" : 'http://' + IOTTPLATFORM_URL_HOST + ':' + IOTPLATFORM_HTTP_PORT + next((path for path in train_paths if SENSOR_ID in path), None),
                    "testDataPath" : 'http://' + IOTTPLATFORM_URL_HOST + ':' + IOTPLATFORM_HTTP_PORT + next((path for path in test_paths if SENSOR_ID in path), None),
                    "sensorParam" : message_content['sensorParam'][SENSOR_ID]
                }
                logger.info(f"sensor_ctrl_msg: {sensor_ctrl_msg}")
                post_iot_platform_sensing_cin(SENSOR_TARGET_CNT_PATH, sensor_ctrl_msg)
                logger.info(f"{SENSOR_ID} data collection start")
                
                # 센서 상태 SUBSCRIBE(현재: nu = url)
                # 질문! nu 값에 주소 전체를 적을지 아니면 topic만 적을지
                SENSOR_STATE_SUBSCRIPTION_RESOURCE_PATH = 'http://' + IOTTPLATFORM_URL_HOST + ':' + IOTPLATFORM_HTTP_PORT + next((path for path in state_paths if SENSOR_ID in path), None)
                logger.info(f"SENSOR_STATE_SUBSCRIPTION_RESOURCE_PATH: {SENSOR_STATE_SUBSCRIPTION_RESOURCE_PATH}")
                nu = next(iter(get_iot_platform_sub_nu(SENSOR_STATE_SUBSCRIPTION_RESOURCE_PATH)))
                logger.info(f"{SENSOR_ID}'s nu: {nu}")

            logger.info(f"{SENSOR_ID} subscribe start")
                # todo change to multi process
            wating_sensor_ready(nu, IOTPLATFORM_MQTT_PORT)

                # process = multiprocessing.Process(target=wating_sensor_ready, args=(nu, IOTPLATFORM_MQTT_PORT))
                # logger.info(f"process: {process}")
                # processes.append(process)
                # process.start()
            # for process in processes:
                # process.join()
            logger.info(f"{SENSOR_ID} subscribe end")
            
            data_hub_train_paths = message_content['dataResourceInformation']['dataHubInformation']['rawTrainDataPath']
            data_hub_test_paths = message_content['dataResourceInformation']['dataHubInformation']['rawTestDataPath']

            for SENSOR_ID in sensor_list:
                each_train_path = 'http://' + IOTTPLATFORM_URL_HOST + ':' + IOTPLATFORM_HTTP_PORT + next((path for path in train_paths if SENSOR_ID in path), None)
                train_data_list = all_cin_get_uri(each_train_path)
                logger.info(f"SENSOR_ID: {SENSOR_ID}")
                logger.info(f"train_data_list: {train_data_list}") 
                each_datahub_train_path = next((path for path in data_hub_train_paths if SENSOR_ID in path), None)
                for train_data in train_data_list:
                # DTO 객체 생성
                    logger.info(f"train_data: {train_data}")
                    response = post_data_hub_sensing_cin(SENSOR_TYPE, DATAHUB_URL_HOST_INGEST_ENDPOINT, each_datahub_train_path, SENSOR_ID, train_data)
                    logger.info(f"response: {response}")
            for SENSOR_ID in sensor_list:
                each_test_path = 'http://' + IOTTPLATFORM_URL_HOST + ':' + IOTPLATFORM_HTTP_PORT + next((path for path in test_paths if SENSOR_ID in path), None)
                test_data_list = all_cin_get_uri(each_test_path)
                each_datahub_test_path = next((path for path in data_hub_test_paths if SENSOR_ID in path), None)
                for test_data in test_data_list:
                # DTO 객체 생성
                    response = post_data_hub_sensing_cin(SENSOR_TYPE, DATAHUB_URL_HOST_INGEST_ENDPOINT, each_datahub_test_path, SENSOR_ID, test_data)
                    logger.info(f"response: {response}")
                

            response_msg = {'eventId': str(uuid.uuid4()), 'eventTransId': message.value['eventTransId'], 'fromSystemName': ENGINE_TYPE, 'toSystemName': 'EAI', 'contentType': 'application/json', 'messageType': 'response', 'content': {'serviceId': SERVICE_ID, 'engineType': ENGINE_TYPE, 'state': 'Done'}}
            send_message(producer, KAFKA_RESPONSE_TOPIC, SERVICE_ID, response_msg)
            logger.info(f"{message.key} done")
        except Exception as e:
            logger.error(f"{message.key} error: {e}")
            continue
    else:
        logger.info(f"{message.key} bypass")
        continue