package ru.sparural.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import ru.sparural.kafka.annotation.EnableSparuralKafkaConsumer;
import ru.sparural.kafka.annotation.EnableSparuralKafkaProducer;

/**
 * @author Vyacheslav Vorobev
 */
@SpringBootApplication
@EnableSparuralKafkaConsumer
@EnableSparuralKafkaProducer
@EnableCaching
@Slf4j
public class EngineApplication implements CommandLineRunner {

    @Autowired
    Environment env;

    public static void main(String[] args) {
        SpringApplication.run(EngineApplication.class, args);
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