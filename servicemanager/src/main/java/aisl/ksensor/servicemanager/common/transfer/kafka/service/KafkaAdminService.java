package aisl.ksensor.servicemanager.common.transfer.kafka.service;

public interface KafkaAdminService {
    void createTopic(String topicName, int numPartitions, short replicationFactor);
    void deleteTopic(String topicName);
    void resetTopicOffset(String topicName, int partition, long offset);
}
