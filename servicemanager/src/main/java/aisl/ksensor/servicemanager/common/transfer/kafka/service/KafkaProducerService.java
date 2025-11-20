package aisl.ksensor.servicemanager.common.transfer.kafka.service;

public interface KafkaProducerService {
    void sendMessage(String topic, String key, String value);
}
