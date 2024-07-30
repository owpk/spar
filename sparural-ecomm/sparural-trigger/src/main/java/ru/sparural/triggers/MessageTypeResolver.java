package ru.sparural.triggers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.user.UserNotificationInfoDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.notification.api.MessageTypes;
import ru.sparural.notification.api.dto.MessageDto;
import ru.sparural.notification.api.dto.email.EmailNotificationDto;
import ru.sparural.notification.api.dto.email.Recipient;
import ru.sparural.notification.api.dto.email.Sender;
import ru.sparural.notification.api.dto.push.PushNotificationDto;
import ru.sparural.notification.api.dto.sms.SmsPushMessageDto;
import ru.sparural.notification.api.dto.viber.ViberPushMessageDto;
import ru.sparural.notification.api.dto.whatsapp.WhatsAppPushMessageDto;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggers.services.ScreenKafkaEngineService;

import java.util.List;
import java.util.UUID;

import ru.sparural.triggers.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTypeResolver {

    private final SparuralKafkaRequestCreator sparuralKafkaRequestCreator;
    private final KafkaTopics kafkaTopics;
    private final ScreenKafkaEngineService screenKafkaEngineService;

    public void resolveAndSend(MessageTemplateDto messageTemplate, UserNotificationInfoDto userNotificationInfo, Long merchantId) {
        log.info("Resolving message with template: " + messageTemplate + " | userId: " + userNotificationInfo.getUser().getId());
        var msgType = messageTemplate.getMessageType();
        switch (msgType) {
            case MessageTypes.PUSH:
                sendPush(messageTemplate, userNotificationInfo, merchantId);
                break;
            case MessageTypes.EMAIL:
                sendEmail(messageTemplate, userNotificationInfo);
                break;
            case MessageTypes.SMS:
                sendSms(messageTemplate, userNotificationInfo);
                break;
            case MessageTypes.VIBER:
                sendViber(messageTemplate, userNotificationInfo);
                break;
            case MessageTypes.WHATSAPP:
                sendWhatsapp(messageTemplate, userNotificationInfo);
                break;

        }
    }

    private void sendSms(MessageTemplateDto messageTemplate, UserNotificationInfoDto userNotificationInfo) {
        var smsPushMessage = new SmsPushMessageDto();
        smsPushMessage.setUserId(userNotificationInfo.getUser().getId());
        smsPushMessage.setMessages(List.of(
                MessageDto.builder()
                        .from(messageTemplate.getName())
                        .text(messageTemplate.getMessage())
                        .to(userNotificationInfo.getUser().getPhoneNumber()).build()));
        sendMessage("send/sms", smsPushMessage);
    }

    private void sendPush(MessageTemplateDto messageTemplate, UserNotificationInfoDto userNotificationInfo, Long merchantId) {
        var req = new PushNotificationDto();
        req.setUserId(userNotificationInfo.getUser().getId());
        req.setMessage(messageTemplate.getMessage());
        req.setName(messageTemplate.getName());
        req.setMerchantId(merchantId);
        var notifScreen = new ru.sparural.notification.api.dto.push.ScreenDto();
        var engineScreen = screenKafkaEngineService.getById(messageTemplate.getScreenId());
        notifScreen.setCode(engineScreen.getCode());
        notifScreen.setName(engineScreen.getName());
        notifScreen.setId(engineScreen.getId());
        req.setScreen(notifScreen);
        req.setLifetime(messageTemplate.getLifetime().longValue());
        req.setBody(messageTemplate.getMessage());
        req.setIsReaded(false);
        req.setTitle(messageTemplate.getName());
        if (!userNotificationInfo.getTokens().isEmpty()) {
            userNotificationInfo.getTokens()
                    .forEach(token -> {
                        req.setDeviceType(token.getDevicetype());
                        req.setPushToken(token.getToken());
                        sendMessage("send/push", req);
                    });
        } else {
            sendMessage("send/push", req);
        }
    }

    private void sendWhatsapp(MessageTemplateDto messageTemplate, UserNotificationInfoDto userNotificationInfo) {
        var whatsAppPushMessage = new WhatsAppPushMessageDto();
        whatsAppPushMessage.setUserId(userNotificationInfo.getUser().getId());
        whatsAppPushMessage.setMessages(List.of(
                MessageDto.builder()
                        .from(messageTemplate.getName())
                        .text(messageTemplate.getMessage())
                        .to(userNotificationInfo.getUser().getPhoneNumber()).build()));
        sendMessage("/request-whatsapp", whatsAppPushMessage);
    }

    private void sendViber(MessageTemplateDto messageTemplate, UserNotificationInfoDto userNotificationInfo) {
        var viberNotification = new ViberPushMessageDto();
        viberNotification.setUserId(userNotificationInfo.getUser().getId());
        viberNotification.setMessages(List.of(
                MessageDto.builder()
                        .from(messageTemplate.getName())
                        .text(messageTemplate.getMessage())
                        .to(userNotificationInfo.getUser().getPhoneNumber()).build()));
        sendMessage("send/viber", viberNotification);
    }

    private void sendEmail(MessageTemplateDto messageTemplate, UserNotificationInfoDto userNotificationInfo) {
        var emailNotification = new EmailNotificationDto();
        emailNotification.setUserId(userNotificationInfo.getUser().getId());
        emailNotification.setMessage(messageTemplate.getMessageHTML());
        emailNotification.setMessageUuid(UUID.randomUUID().toString());
        emailNotification.setSender(new Sender(messageTemplate.getSubject(), messageTemplate.getName()));
        emailNotification.setSubject(messageTemplate.getSubject());
        emailNotification.setRecipients(List.of(new Recipient(userNotificationInfo.getUser().getEmail(), userNotificationInfo.getUser().getFirstName())));
        sendMessage("send/email", emailNotification);
    }

    private int resolveUrgency(MessageTemplateDto messageTemplateDto) {
        int level = 0;
        if (messageTemplateDto.getIsSystem()) level = 1;
        return level;
    }

    private void sendMessage(String action, Object req) {
        sparuralKafkaRequestCreator.createRequestBuilder()
                .withAction(action)
                .withTopicName(kafkaTopics.getNotificationRequestMainTopicName())
                .withRequestBody(req)
                .sendAsync();
    }
}
