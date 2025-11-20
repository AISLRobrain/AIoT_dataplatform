package aisl.ksensor.servicemanager.common.transfer.kafka.service.impl;

import aisl.ksensor.servicemanager.common.transfer.kafka.service.KafkaConsumerService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final String topic = "testTopic1";
    private final String keyToFilter = "aislOpt";

    public void consume() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "203.250.148.120:20517");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup1");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Consumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            records.records(topic).forEach(record -> {
                if (record.key().equals(keyToFilter)) {
                    System.out.printf("Consumed record with key %s and value %s%n", record.key(), record.value());
                }
            });
        }
    }
}
