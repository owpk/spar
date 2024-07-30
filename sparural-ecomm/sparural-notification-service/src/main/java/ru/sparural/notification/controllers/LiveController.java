package ru.sparural.notification.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.NotificationDto;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.notification.config.SettingsBeanFactory;
import ru.sparural.notification.api.dto.ws.WSNotificationRequestDto;
import ru.sparural.notification.service.impl.MainNotificationService;

/**
 * @author Vorobyev Vyacheslav
 */
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.notification.main}")
@RequiredArgsConstructor
@Slf4j
public class LiveController {

    private final SettingsBeanFactory settingsBeanFactory;
    private final MainNotificationService mainNotificationService;

    @KafkaSparuralMapping("alive")
    public NotificationDto isAlive() {
        var dto = settingsBeanFactory.getConfigBean().getNotificationDto();
        log.info("CURRENT SETTINGS: " + dto);
        return dto;
    }

    @KafkaSparuralMapping("test/ws")
    public void sendWs(@RequestParam Long userId) {
        var msg = new WSNotificationRequestDto();
        msg.setName("TEST");
        msg.setMessage("TEST");
        mainNotificationService.ws(userId, msg);
    }

}
