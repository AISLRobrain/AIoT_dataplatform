from kafka import KafkaConsumer, KafkaProducer

import json

def setup_consumer(host, port, topic, group_id):
    consumer = KafkaConsumer(topic,
                                bootstrap_servers=[f"{host}:{port}"],
                                auto_offset_reset='earliest',
                                enable_auto_commit=True,
                                group_id=group_id,
                                value_deserializer=lambda x: json.loads(x.decode('utf-8')),
                                key_deserializer=lambda x: x.decode('utf-8'))
    return consumer

def setup_producer(host, port):
    producer = KafkaProducer(
        bootstrap_servers=[f"{host}:{port}"],
        value_serializer=lambda x: json.dumps(x).encode('utf-8'),
        key_serializer=lambda x: x.encode('utf-8')
    )
    return producer

def send_message(producer, topic, key, message):
    producer.send(topic, key=key, value=message)
    producer.flush()




if __name__ == '__main__':

    # Kafka 설정
    KAFKA_HOST = 'localhost'
    KAFKA_PORT = '9092'
    KAFKA_TOPIC = 'test1'
    KAFKA_GROUP_ID = 'test_group'

    # Consumer 및 Producer 설정
    consumer = setup_consumer(KAFKA_HOST, KAFKA_PORT, KAFKA_TOPIC, KAFKA_GROUP_ID)
    producer = setup_producer(KAFKA_HOST, KAFKA_PORT)

    # 메시지 전송 예시 (필요한 경우 주석 해제)
    send_message(producer, KAFKA_TOPIC, 'general', {'test': 'test_value'})


    for message in consumer:
        if message.key == 'general':
            print(message.offset, message.key, message.value)
            break
    print('end')