package io.tpd.kafkabasics.producer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerSubscriber {

    @KafkaListener(topics = "${tpd.kafka.customers-topic-name}",
            clientIdPrefix = "${tpd.kafka.consumer-name}",
            groupId = "${tpd.kafka.consumer-name}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAsObject(ConsumerRecord<String, Customer> cr,
                               @Payload Customer customer) {
        log.info("[key: {}, offset:{}] Customer received via Kafka: {}", cr.key(), cr.offset(), customer);
    }
}
