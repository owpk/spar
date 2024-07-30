package ru.sparural.backgrounds.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sparural.backgrounds.cache.EngineServiceCacheManager;
import ru.sparural.kafka.producer.KafkaSparuralProducer;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

@Configuration
public class BeanConfiguration {
    
    @Bean
    public SparuralKafkaRequestCreator requestCreator(KafkaSparuralProducer kafkaSparuralProducer) {
        return new SparuralKafkaRequestCreator(kafkaSparuralProducer);
    }

    @Bean(name = "EngineCacheManager")
    public CacheManager engineCacheManager() {
        return new EngineServiceCacheManager();
    }

}
