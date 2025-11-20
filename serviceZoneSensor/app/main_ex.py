import configparser
import os
import logging
import uuid
import numpy as np

from data_base.common_postgres import connect_to_database, get_table_data, record_exists
from kafka_broker.common_kafka import setup_consumer, setup_producer, send_message
from module_import.import_filter import dynamic_import_from_directory
from api_util.http_request import post_request
from api_util.util import make_entity_vo, generate_hash_id, extract_entity_id_array


# Config 파일 로드
config = configparser.ConfigParser()
config.read('./config/config.ini')

# COMMON 섹션에서 기본 변수 로드
ENGINE_TYPE = config['COMMON']['ENGINE_TYPE']

# DATABASE 섹션에서 기본 변수 로드
DATABASE_NAME = config['DATABASE']['DATABASE_NAME']
SCHEMA_NAME = config['DATABASE']['SCHEMA_NAME']
USER = config['DATABASE']['USER']
PASSWORD = config['DATABASE']['PASSWORD']
HOST = config['DATABASE']['HOST']
PORT = config['DATABASE']['PORT']
FIELD_NAME = config['DATABASE']['FIELD_NAME']

# KAFKA 섹션에서 기본 변수 로드
KAFKA_HOST = config['KAFKA']['KAFKA_HOST']
KAFKA_PORT = config['KAFKA']['KAFKA_PORT']
KAFKA_REQUEST_TOPIC = config['KAFKA']['KAFKA_REQUEST_TOPIC']
KAFKA_RESPONSE_TOPIC = config['KAFKA']['KAFKA_RESPONSE_TOPIC']
KAFKA_GROUP_ID = config['KAFKA']['KAFKA_GROUP_ID']

# API 섹션에서 기본 변수 로드
INTERFACE_URL = config['API']['INTERFACE_URL']
PUSH_ENTITY_ENDPOINT = config['API']['ENDPOINT']

SERVICE_ID = os.environ['SERVICE_ID']
TABLE_NAME = os.environ['SENSOR_TYPE']

logger = logging.getLogger(SERVICE_ID + "\'s" + ENGINE_TYPE +"Logger")
handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.INFO)

logger.info(f"Service ID: {SERVICE_ID}")

# 필터 모듈 동적 로딩
imported_modules = dynamic_import_from_directory('./filter_repository')
logger.info(f"Imported Modules: {imported_modules}")


KAFKA_GROUP_ID = SERVICE_ID

# Consumer 및 Producer 설정
consumer = setup_consumer(KAFKA_HOST, KAFKA_PORT, KAFKA_REQUEST_TOPIC, KAFKA_GROUP_ID)
producer = setup_producer(KAFKA_HOST, KAFKA_PORT)

response_notification = {
    "eventId": uuid.uuid4().hex,
    "eventTransId": uuid.uuid4().hex,
    "fromSystemName": "System1",
    "toSystemName": "System2",
    "contentType": "application/json",
    "content": {
        "serviceId": SERVICE_ID,
        "engineType": ENGINE_TYPE
    }
}


for message in consumer:
    if message.key == SERVICE_ID:
        logger.info(f"{message.key}'s Optimization Transaction: {message.offset}")
        logger.info(f"Message: {message.value}")

        whole_optimization_info = message.value["content"]

        sensor_parameter = whole_optimization_info['sensorParameter']
        filter_parameter = whole_optimization_info['filterParameter']

        sensor_filter_parameter = {
            "sensorParameter": sensor_parameter,
        }

        sensor_filter_parameter = {
            "sensorParameter": sensor_filter_parameter,
            "filterParameter": filter_parameter
        }

        # DB access(get data)
        hashed_raw_dataset = SERVICE_ID+generate_hash_id(sensor_filter_parameter)
        hashed_filtered_dataset = SERVICE_ID+generate_hash_id(sensor_filter_parameter)

        # for ACID, connection defined here
        datahub_db_connection = connect_to_database(DATABASE_NAME, USER, PASSWORD, HOST, PORT)
        if record_exists(datahub_db_connection, TABLE_NAME, SCHEMA_NAME, FIELD_NAME, hashed_filtered_dataset):
            logger.info(f"Data is already exist.")
        else:
            logger.info(f"Data is not ready so start filtering.")
            # DB access(get data)
            data = np.array(get_table_data(datahub_db_connection, TABLE_NAME, SCHEMA_NAME, FIELD_NAME, hashed_raw_dataset))[:,0]
            datahub_db_connection.close()

            # ['urn:12132:1123232:[12312,4124214,51242]' 'urn:12132:[3123,5142,123213]']
            sensor_id, target, extracted_data = extract_entity_id_array(data)
            # [list([12312, 3123]) list([123213, 412231, 12321])]

            for idx, each_entity in enumerate(extracted_data):
                filterd_entity = each_entity
                for selected_filter in filter_parameter.keys():
                    # filtering
                    filterd_entity = imported_modules[selected_filter].foward(np.array(filterd_entity), filter_parameter[selected_filter])
                urn_data = sensor_id[idx] + ":" + target[idx] + ":" + str(filterd_entity.tolist())
                # ingest interface
                entity_vo = make_entity_vo(hashed_filtered_dataset, urn_data)
                response_post = post_request(INTERFACE_URL+PUSH_ENTITY_ENDPOINT, json = entity_vo)

                logger.info(f"Response: {response_post.json()}")

        # Kafka에 결과 전송
        send_message(producer, KAFKA_RESPONSE_TOPIC, SERVICE_ID, response_notification)
        logger.info(f"Optimization Transaction: {message.offset}'s filtering is done.")