package io.tpd.kafkabasics.producer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerSubscriber {

    boolean firstTime = true;

    @KafkaListener(topics = "${tpd.kafka.customers-topic-name}",
            clientIdPrefix = "${tpd.kafka.consumer-name}",
            groupId = "${tpd.kafka.consumer-name}",
            errorHandler = "customersErrorHandler",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAsObject(ConsumerRecord<String, Customer> cr,
                               @Payload Customer customer) {
        log.info("[key: {}, offset:{}] Customer received via Kafka: {}", cr.key(), cr.offset(), customer);
        if (customer.getName().contains("john") && firstTime) {
            firstTime = false;
            throw new RuntimeException("I don't like John the first time");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Bean
    public ConsumerAwareListenerErrorHandler customersErrorHandler() {
        return (m, e, c) -> {
            log.error("Error consuming a message, let's retry the last 5!", e);
            MessageHeaders headers = m.getHeaders();
            c.seek(new org.apache.kafka.common.TopicPartition(
                            headers.get(KafkaHeaders.RECEIVED_TOPIC, String.class),
                            headers.get(KafkaHeaders.RECEIVED_PARTITION_ID, Integer.class)),
                    Math.max(0, headers.get(KafkaHeaders.OFFSET, Long.class) - 5)); // panic! replay last 5
            return null;
        };
    }
}
