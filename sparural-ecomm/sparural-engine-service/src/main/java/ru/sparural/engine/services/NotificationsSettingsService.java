package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.NotificationDto;
import ru.sparural.engine.entity.Notification;

/**
 * @author Vorobyev Vyacheslav
 */
public interface NotificationsSettingsService<T> {
    T get();

    T update(T data);

    NotificationDto convertToDto(Notification notification);

    Notification convertToEntity(NotificationDto notificationDto);

    NotificationDto getNotifications();
}
