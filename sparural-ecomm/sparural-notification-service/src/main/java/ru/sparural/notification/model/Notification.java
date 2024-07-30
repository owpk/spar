package ru.sparural.notification.model;

import ru.sparural.notification.model.push.Screen;

/**
 * @author Vorobyev Vyacheslav
 */
public interface Notification extends NotificationIdAware {
    String getBody();
    String getTitle();
    Screen getScreen();
    String getType();
    Long getUserId();
    Long getMerchantId();
}