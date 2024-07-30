package ru.sparural.file.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sparural.file.service.store.DiskFileStore;
import ru.sparural.file.service.store.FileStore;
import ru.sparural.kafka.producer.KafkaSparuralProducer;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

@Configuration
@RequiredArgsConstructor
public class FileServiceConfig {

    @Bean("fileStore")
    @ConditionalOnProperty(prefix = "storage", name = "type", havingValue = "disk")
    public FileStore diskFileStore() throws InstantiationException, IllegalAccessException {
        return new DiskFileStore();
    }

    @Bean
    public SparuralKafkaRequestCreator kafkaRequestCreator(KafkaSparuralProducer kafkaSparuralProducer) {
        return new SparuralKafkaRequestCreator(kafkaSparuralProducer);
    }
}
