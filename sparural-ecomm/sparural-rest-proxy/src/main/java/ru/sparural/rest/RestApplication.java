package ru.sparural.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import ru.sparural.kafka.annotation.EnableSparuralKafkaProducer;
import ru.sparural.kafka.producer.KafkaSparuralProducer;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

@EnableSparuralKafkaProducer
@SpringBootApplication
@EnableCaching
@Slf4j
public class RestApplication implements CommandLineRunner {

    @Autowired
    Environment env;

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    @Bean
    public SparuralKafkaRequestCreator sparuralKafkaRequestCreator(KafkaSparuralProducer kafkaSparuralProducer) {
        return new SparuralKafkaRequestCreator(kafkaSparuralProducer);
    }

    @Override
    public void run(String... args) {
        for (org.springframework.core.env.PropertySource<?> source : ((AbstractEnvironment) env).getPropertySources()) {
            if (source instanceof OriginTrackedMapPropertySource) {
                var mapped = (OriginTrackedMapPropertySource) source;
                log.info(":: PROPS CONFIG: " + mapped.getName());
                mapped.getSource().forEach((k, v) -> log.info("{} : {}", k, v));
            }
        }
    }
}