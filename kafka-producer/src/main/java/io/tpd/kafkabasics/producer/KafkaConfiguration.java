package io.tpd.kafkabasics.producer;

import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfiguration {

    // Producer configuration
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(props,
                new StringSerializer(),
                new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public NewTopic customersTopic(@Value("${tpd.kafka.customers-topic-name}") String customersTopicName,
                                   @Value("${tpd.kafka.customers-topic-partitions}") int partitions) {
        return TopicBuilder.name(customersTopicName)
                .partitions(partitions)
                .replicas(1)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
                // Very low retention to make sure the consumers don't see the tombstones after compacted
                .config(TopicConfig.DELETE_RETENTION_MS_CONFIG, "100")
                // Reduced value to make sure there are new logs and therefore old data to compact
                .config(TopicConfig.SEGMENT_MS_CONFIG, "100")
                // Ratio for cleanup (log.cleaner.min.cleanable.ratio) is 0.5 by default in the broker
                .config(TopicConfig.MIN_CLEANABLE_DIRTY_RATIO_CONFIG, "0.01")
                // The minimum time a message will remain uncompacted in the log
                .config(TopicConfig.MIN_COMPACTION_LAG_MS_CONFIG, "10000")
                .build();
    }


}
