package ru.sparural.triggers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.sparural.kafka.annotation.EnableSparuralKafkaConsumer;
import ru.sparural.kafka.annotation.EnableSparuralKafkaProducer;

/**
 * @author Vorobyev Vyacheslav
 */
@SpringBootApplication
@EnableSparuralKafkaConsumer
@EnableSparuralKafkaProducer
public class TriggerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TriggerServiceApplication.class, args);
    }

}