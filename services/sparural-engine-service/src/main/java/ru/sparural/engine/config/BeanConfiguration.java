package ru.sparural.engine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sparural.engine.loymax.cache.LoymaxCache;
import ru.sparural.engine.loymax.cache.LoymaxCacheManager;
import ru.sparural.engine.loymax.rest.LoymaxRestClient;
import ru.sparural.kafka.producer.KafkaSparuralProducer;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.utils.rest.RestTemplate;

/**
 * @author Vorobyev Vyacheslav
 */
@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final LoymaxCache loymaxCache;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(LoggerFactory.getLogger(LoymaxRestClient.class));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @Primary
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
        modelMapper.getConfiguration().setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnProperty(name = "cache.local", havingValue = "true")
    public CacheManager loymaxCacheManager(LoymaxCache loymaxCache) {
        return new LoymaxCacheManager(loymaxCache);
    }

    @Bean
    public SparuralKafkaRequestCreator requestCreator(KafkaSparuralProducer kafkaSparuralProducer) {
        return new SparuralKafkaRequestCreator(kafkaSparuralProducer);
    }

}