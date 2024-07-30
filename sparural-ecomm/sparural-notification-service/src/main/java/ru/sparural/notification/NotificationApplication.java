package ru.sparural.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.sparural.kafka.annotation.EnableSparuralKafkaConsumer;
import ru.sparural.kafka.annotation.EnableSparuralKafkaProducer;
import ru.sparural.kafka.utils.KafkaAdminProvider;

/**
 * @author Vyacheslav Vorobev
 */
@SpringBootApplication
@EnableSparuralKafkaConsumer
@EnableSparuralKafkaProducer
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class NotificationApplication implements CommandLineRunner {
    private final Environment env;
    private final KafkaAdminProvider kafkaAdminProvider;
    private final ApplicationContext ctx;

    @Value("${sparural.kafka.request-topics.notification.main}")
    private String mainTopic;

    @Value("${sparural.kafka.request-topics.notification.required}")
    private String reqTopic;

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }

    @Override
    public void run(String... args) {
        kafkaAdminProvider.initKafkaAdmin(ctx);
        kafkaAdminProvider.createOrModifyTopic(reqTopic, 1);
        kafkaAdminProvider.createOrModifyTopic(mainTopic, 1);
        for (org.springframework.core.env.PropertySource<?> source : ((AbstractEnvironment) env).getPropertySources()) {
            if (source instanceof OriginTrackedMapPropertySource) {
                var mapped = (OriginTrackedMapPropertySource) source;
                log.info(":: PROPS CONFIG: " + mapped.getName());
                mapped.getSource().forEach((k, v) -> log.info("{} : {}", k, v));
            }
        }
    }
}