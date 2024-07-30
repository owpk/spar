package ru.sparural.notification.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.NotificationDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.notification.config.NotificationServiceConfigBean;

import java.util.Arrays;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ServiceSettingsBeanRefreshScheduler {

    private final GenericApplicationContext ctx;
    private final SparuralKafkaRequestCreator sparuralKafkaRequestCreator;

    @Value("${sparural.kafka.request-topics.engine}")
    private final String engineRequestTopicName;

    @Scheduled(fixedRate = 60000)
    public void requestSettingsBean() {
        log.info("Attempting to refresh settings bean");
        try {
            NotificationDto settingsDto = sparuralKafkaRequestCreator.createRequestBuilder()
                    .withTopicName(engineRequestTopicName)
                    .withAction("notifications-settings/get")
                    .sendForEntity();
            var settingsBean = new NotificationServiceConfigBean(settingsDto);
            var factory = (DefaultSingletonBeanRegistry) ctx.getBeanFactory();
            synchronized (ctx) {
                Arrays.stream(ctx.getBeanDefinitionNames())
                        .forEach(x -> {
                            if (x.equals(NotificationServiceConfigBean.DEFAULT_BEAN_NAME)) {
                                factory.destroySingleton(NotificationServiceConfigBean
                                        .DEFAULT_BEAN_NAME);
                            }
                        });
                factory.registerSingleton(NotificationServiceConfigBean.DEFAULT_BEAN_NAME, settingsBean);
            }
            log.info("Notification settings bean has been refreshed. Current settings: " +
                    ctx.getBean(NotificationServiceConfigBean.DEFAULT_BEAN_NAME));
        } catch (Exception e) {
            log.error("Notification settings bean creation error", e);
        }
    }
}
