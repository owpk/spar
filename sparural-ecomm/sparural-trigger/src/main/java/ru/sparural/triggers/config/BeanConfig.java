package ru.sparural.triggers.config;

import org.jooq.conf.RenderNameCase;
import org.jooq.conf.Settings;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.sparural.kafka.producer.KafkaSparuralProducer;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;


/**
 * @author Vorobyev Vyacheslav
 */
@Configuration
public class BeanConfig {

    @Bean
    public SparuralKafkaRequestCreator sparuralKafkaRequestCreator(KafkaSparuralProducer kafkaSparuralProducer) {
        return new SparuralKafkaRequestCreator(kafkaSparuralProducer);
    }

    @Bean
    public Settings jooqSettings() {
        return new Settings().withRenderNameCase(RenderNameCase.LOWER);
    }

    @Bean
    @Primary
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
        modelMapper.getConfiguration().setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }
}
