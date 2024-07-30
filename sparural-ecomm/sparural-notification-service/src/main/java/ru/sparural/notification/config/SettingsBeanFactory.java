package ru.sparural.notification.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.sparural.notification.config.NotificationServiceConfigBean;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
public class SettingsBeanFactory {
    private final ApplicationContext applicationContext;

    public NotificationServiceConfigBean getConfigBean() {
        return applicationContext.getBean(NotificationServiceConfigBean.class);
    }
}