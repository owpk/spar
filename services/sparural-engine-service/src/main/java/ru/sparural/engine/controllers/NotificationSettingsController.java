package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.NotificationSettingsDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.NotificationSetting;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class NotificationSettingsController {
    private final UserService userService;
    private final DtoMapperUtils mapperUtils;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("notification-settings/create")
    public UserProfileDto create(@Payload NotificationSettingsDto notificationSettingsDto,
                                 @RequestParam Long userId) {
        var user = userService.findById(userId);
        var settings = mapperUtils.convert(notificationSettingsDto, NotificationSetting.class);
        userService.updateUserNotificationsSettings(settings, user);
        var profile = userService.getUserProfileInfo(userId);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.USER_PHOTO, profile.getId());
        if (!files.isEmpty()) {
            profile.setPhoto(files.get(files.size() - 1));
        }
        return profile;
    }
}
