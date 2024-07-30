package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.NotificationDto;
import ru.sparural.engine.entity.Notification;
import ru.sparural.engine.services.NotificationsSettingsService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class NotificationsSettingsController {

    private final NotificationsSettingsService<Notification> notificationsSettingsService;

    @KafkaSparuralMapping("notifications-settings/get")
    public NotificationDto list() {
        return notificationsSettingsService.convertToDto(
                notificationsSettingsService.get());
    }

    @KafkaSparuralMapping("notifications-settings/update")
    public NotificationDto update(@Payload NotificationDto data) {
        var entity = notificationsSettingsService.convertToEntity(data);
        var dto = notificationsSettingsService.update(entity);
        return notificationsSettingsService.convertToDto(dto);
    }
}
