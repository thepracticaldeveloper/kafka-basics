package io.tpd.kafkabasics.producer;

import lombok.*;

@Value
@With
public class Customer {
    long id;
    String name;
}
