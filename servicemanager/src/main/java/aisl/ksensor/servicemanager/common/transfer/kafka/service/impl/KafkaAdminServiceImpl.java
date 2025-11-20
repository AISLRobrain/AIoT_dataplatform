package aisl.ksensor.servicemanager.common.transfer.kafka.service.impl;

import aisl.ksensor.servicemanager.common.transfer.kafka.service.KafkaAdminService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;

public class KafkaAdminServiceImpl implements KafkaAdminService {
    private final AdminClient adminClient;

    public KafkaAdminServiceImpl(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @Override
    public void createTopic(String topicName, int numPartitions, short replicationFactor) {
        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        adminClient.createTopics(Collections.singletonList(newTopic));
    }

    @Override
    public void deleteTopic(String topicName) {
        adminClient.deleteTopics(Collections.singletonList(topicName));
    }

    @Override
    public void resetTopicOffset(String topicName, int partition, long offset) {
        // 이 메서드는 조금 더 복잡하며, Consumer를 사용하여 오프셋을 재설정해야 합니다.
        // 여기서는 간단한 예시로만 제공합니다.
        TopicPartition topicPartition = new TopicPartition(topicName, partition);
        // ... 해당 파티션의 오프셋을 재설정하는 로직 ...
    }

    // 추가로 필요한 메서드들의 구현...
}
