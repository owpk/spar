package ru.sparural.notification.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.notification.api.dto.NotificationResponseDto;
import ru.sparural.notification.api.dto.email.EmailNotificationDto;
import ru.sparural.notification.api.dto.push.PushNotificationDto;
import ru.sparural.notification.api.dto.sms.SmsPushMessageDto;
import ru.sparural.notification.api.dto.viber.ViberPushMessageDto;
import ru.sparural.notification.api.dto.whatsapp.WhatsAppPushMessageDto;
import ru.sparural.notification.api.dto.ws.WSNotificationRequestDto;
import ru.sparural.notification.model.email.EmailNotification;
import ru.sparural.notification.model.push.PushNotification;
import ru.sparural.notification.model.sms.SmsPushMessage;
import ru.sparural.notification.model.viber.ViberPushMessage;
import ru.sparural.notification.model.whatsapp.WhatsAppPushMessage;
import ru.sparural.notification.service.impl.MainNotificationService;

/**
 * @author Vorobyev Vyacheslav
 */
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.notification.required}", idleInterval = "${topic.idle_interval.required}")
@RequiredArgsConstructor
public class RequiredNotificationController {

    private final MainNotificationService mainNotificationService;
    private final ModelMapper modelMapper;

    @KafkaSparuralMapping("send/push")
    public NotificationResponseDto push(@Payload PushNotificationDto notificationRequestDto) {
        return mainNotificationService.push(modelMapper.map(notificationRequestDto, PushNotification.class));
    }

    @KafkaSparuralMapping("send/viber")
    public NotificationResponseDto viber(@Payload ViberPushMessageDto viberPushMessage) {
        return mainNotificationService.viber(modelMapper.map(viberPushMessage, ViberPushMessage.class));
    }

    @KafkaSparuralMapping("send/whatsapp")
    public NotificationResponseDto whatsapp(@Payload WhatsAppPushMessageDto whatsAppPushMessage) {
        return mainNotificationService.whatsapp(modelMapper.map(whatsAppPushMessage, WhatsAppPushMessage.class));
    }

    @KafkaSparuralMapping("send/email")
    public NotificationResponseDto email(@Payload EmailNotificationDto notificationRequestDto) {
        return mainNotificationService.email(modelMapper.map(notificationRequestDto, EmailNotification.class));
    }

    @KafkaSparuralMapping("send/ws")
    public void ws(@RequestParam Long userId, @Payload WSNotificationRequestDto notificationRequestDto) {
        mainNotificationService.ws(userId, notificationRequestDto);
    }

    @KafkaSparuralMapping("send/sms")
    public NotificationResponseDto sms(@Payload SmsPushMessageDto smsPushMessage) {
        return mainNotificationService.sms(modelMapper.map(smsPushMessage, SmsPushMessage.class));
    }
}
