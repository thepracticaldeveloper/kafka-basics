package io.tpd.kafkabasics.producer;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final AtomicLong idSequence;
    private final CustomerPublisher customerPublisher;

    public CustomerController(final CustomerPublisher customerPublisher) {
        this.idSequence = new AtomicLong(1);
        this.customerPublisher = customerPublisher;
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        // Doesn't save anything, but it simulates it by creating an identifier
        var savedCustomer = customer.withId(idSequence.getAndIncrement());
        log.info("Creating new customer: {}", savedCustomer);
        customerPublisher.publish(savedCustomer);
        return savedCustomer;
    }

    @PutMapping("/{id}")
    public Customer update(@RequestBody Customer customer,
                           @PathVariable("id") long id) {
        // Doesn't update anything
        var savedCustomer = customer.withId(id);
        log.info("Updating customer: {}", savedCustomer);
        customerPublisher.publish(savedCustomer);
        return savedCustomer;
    }

    @PostMapping("/sample")
    public void sampleData() throws InterruptedException {
        var c1 = new Customer(1, "john");
        var c2 = new Customer(2, "rachel");
        var c3 = new Customer(3, "michael");
        var c4 = new Customer(4, "anna");
        var c5 = new Customer(5, "jacinta");
        var c6 = new Customer(4, "anna");
        var c7 = new Customer(3, "michael");
        var c8 = new Customer(1, "john");
        var c9 = new Customer(2, "rachel");
        var c10 = new Customer(1, "john");
        customerPublisher.publish(c1);
        Thread.sleep(200);
        customerPublisher.publish(c2);
        Thread.sleep(200);
        customerPublisher.publish(c3);
        Thread.sleep(200);
        customerPublisher.publish(c4);
        Thread.sleep(200);
        customerPublisher.publish(c5);
        Thread.sleep(200);
        customerPublisher.publish(c6);
        Thread.sleep(200);
        customerPublisher.publish(c7);
        Thread.sleep(200);
        customerPublisher.publish(c8);
        Thread.sleep(200);
        customerPublisher.publish(c9);
        Thread.sleep(200);
        customerPublisher.publish(c10);
    }

}
