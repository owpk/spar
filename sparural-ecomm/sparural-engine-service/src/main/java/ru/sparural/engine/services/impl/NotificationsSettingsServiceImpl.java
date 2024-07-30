package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.NotificationDto;
import ru.sparural.engine.entity.Notification;
import ru.sparural.engine.repositories.NotificationsRepository;
import ru.sparural.engine.services.NotificationsSettingsService;
import ru.sparural.engine.utils.DtoMapperUtils;

@Service
@RequiredArgsConstructor
public class NotificationsSettingsServiceImpl implements NotificationsSettingsService<Notification> {

    private final NotificationsRepository notificationsRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public Notification get() {
        return notificationsRepository.get();
    }

    @Override
    public Notification update(Notification data) {
        return notificationsRepository.update(data);
    }

    @Override
    public NotificationDto convertToDto(Notification notification) {
        return dtoMapperUtils.convert(notification, NotificationDto.class);
    }

    @Override
    public Notification convertToEntity(NotificationDto notificationDto) {
        return dtoMapperUtils.convert(notificationDto, Notification.class);
    }

    @Override
    public NotificationDto getNotifications() {
        return convertToDto(get());
    }

}