package ru.sparural.notification.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import ru.sparural.kafka.producer.KafkaSparuralProducer;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.notification.NotificationApplication;
import ru.sparural.notification.config.NotificationServiceConfigBean;
import ru.sparural.utils.rest.RestTemplate;

/**
 * @author Vorobyev Vyacheslav
 */
@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(LoggerFactory.getLogger(NotificationApplication.class));
    }

    @Bean(name = NotificationServiceConfigBean.DEFAULT_BEAN_NAME)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public NotificationServiceConfigBean notificationServiceConfigBean() {
        return new NotificationServiceConfigBean();
    }

    @Bean("KafkaRequestCreator")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SparuralKafkaRequestCreator requestCreator(KafkaSparuralProducer kafkaSparuralProducer) {
        return new SparuralKafkaRequestCreator(kafkaSparuralProducer);
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
}