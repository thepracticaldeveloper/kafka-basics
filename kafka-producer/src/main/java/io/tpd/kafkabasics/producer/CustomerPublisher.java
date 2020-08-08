package io.tpd.kafkabasics.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerPublisher {

    private final KafkaTemplate<String, Object> template;
    private final String topicName;

    public CustomerPublisher(final KafkaTemplate<String, Object> template,
                             @Value("${tpd.kafka.customers-topic-name}") final String topicName) {
        this.template = template;
        this.topicName = topicName;
    }

    public void publish(final Customer customer) {
        template.send(topicName, String.valueOf(customer.getId()), customer);
    }
}
