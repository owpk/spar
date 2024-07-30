package ru.sparural.notification.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.NotificationsDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.notification.api.constants.DeviceType;
import ru.sparural.notification.api.constants.MessageStatus;
import ru.sparural.notification.api.dto.NotificationResponseDto;
import ru.sparural.notification.api.dto.ws.WSNotificationRequestDto;
import ru.sparural.notification.model.Notification;
import ru.sparural.notification.model.email.EmailNotification;
import ru.sparural.notification.model.push.PushNotification;
import ru.sparural.notification.model.sms.SmsPushMessage;
import ru.sparural.notification.model.viber.ViberPushMessage;
import ru.sparural.notification.model.whatsapp.WhatsAppPushMessage;
import ru.sparural.notification.service.UserPushTokenService;
import ru.sparural.notification.service.impl.devino.DevinoServiceRestClient;
import ru.sparural.notification.service.impl.email.EmailNotificationService;
import ru.sparural.notification.service.impl.email.EmailRecipients;
import ru.sparural.notification.service.impl.email.EmailSenderInfo;
import ru.sparural.notification.service.impl.streamtelecom.StreamTelecomRestClient;
import ru.sparural.notification.service.impl.streamtelecom.dto.SmsMessage;
import ru.sparural.notification.service.impl.streamtelecom.dto.StreamTelekomSmsPushDto;
import ru.sparural.notification.utils.CheckedRunnable;
import ru.sparural.notification.websocket.WebSocketProtocolHandler;
import ru.sparural.notification.websocket.WebSocketPushService;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import ru.sparural.notification.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MainNotificationService {
    private static final Runnable EMPTY_RUNNABLE = () -> {
    };

    private final EmailNotificationService emailNotificationService;
    private final PushNotificationDispatcherCommon pushNotificationDispatcherCommon;
    private final DevinoServiceRestClient devinoServiceRestClient;
    private final WebSocketPushService webSocketPushService;
    private final StreamTelecomRestClient streamTelecomRestClient;
    private final WebSocketProtocolHandler protocolHandler;
    private final UserPushTokenService userPushTokenService;
    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final KafkaTopics kafkaTopics;

    public NotificationResponseDto push(PushNotification pushNotification) {
        saveNotificationAndSetNotificationId(pushNotification);
        var merchantId = pushNotification.getMerchantId() == null ?
                "" : pushNotification.getMerchantId().toString();
        return sendAndSetResponse(() -> {
                    if (checkIfWebSocketConnected(pushNotification.getUserId())) {
                        var msg = new WSNotificationRequestDto();
                        msg.setName(pushNotification.getName());
                        msg.setMessage(pushNotification.getMessage());
                        msg.setMerchantId(merchantId);
                        msg.setNotificationId(pushNotification.getNotificationId());
                        msg.setClick_action(pushNotification.getScreen().getCode());
                        ws(pushNotification.getUserId(), msg);
                    } else {

                        var service = pushNotificationDispatcherCommon.getServiceByDeviceType(
                                DeviceType.of(pushNotification.getDeviceType()));

                        service.send(pushNotification,
                                Map.of(
                                        "merchant_id", merchantId,
                                        "click_action", pushNotification.getScreen().getCode()
                                ));
                    }
                },
                () -> userPushTokenService.deleteByToken(pushNotification.getPushToken()),
                pushNotification.getNotificationId());
    }

    public NotificationResponseDto viber(ViberPushMessage viberPushMessage) {
        saveNotificationAndSetNotificationId(viberPushMessage);
        return sendAndSetResponse(() -> devinoServiceRestClient.sendViber(viberPushMessage),
                EMPTY_RUNNABLE,
                viberPushMessage.getNotificationId());
    }

    public NotificationResponseDto whatsapp(WhatsAppPushMessage whatsAppPushMessage) {
        saveNotificationAndSetNotificationId(whatsAppPushMessage);
        return sendAndSetResponse(() -> devinoServiceRestClient.sendWhatsApp(whatsAppPushMessage),
                EMPTY_RUNNABLE,
                whatsAppPushMessage.getNotificationId());
    }

    public NotificationResponseDto email(EmailNotification notificationRequest) {
        saveNotificationAndSetNotificationId(notificationRequest);
        var emailSender = new EmailSenderInfo();
        emailSender.setAddress(notificationRequest.getSender().getAddress());
        emailSender.setName(notificationRequest.getSender().getName());
        return sendAndSetResponse(() ->
                        emailNotificationService.sendEmail(notificationRequest.getRecipients().stream()
                                        .map(x -> new EmailRecipients(x.getAddress(), x.getName()))
                                        .collect(Collectors.toList()),
                                emailSender, notificationRequest.getSubject(), notificationRequest.getMessage()),
                EMPTY_RUNNABLE,
                notificationRequest.getNotificationId());
    }

    public void ws(Long userId, WSNotificationRequestDto notificationRequestDto) {
        webSocketPushService.sendPush(userId, notificationRequestDto);
    }

    public NotificationResponseDto sms(SmsPushMessage smsPushMessage) {
        saveNotificationAndSetNotificationId(smsPushMessage);
        return sendAndSetResponse(() -> {
            var streamTelekomPushMsg = new StreamTelekomSmsPushDto();
            streamTelekomPushMsg.setMessages(smsPushMessage.getMessages().stream()
                    .map(x -> {
                        var smsMsg = new SmsMessage();
                        smsMsg.setData(x.getText());
                        smsMsg.setDestinationAddress(x.getTo());
                        smsMsg.setSourceAddress(x.getFrom());
                        return smsMsg;
                    }).collect(Collectors.toList()));
            streamTelecomRestClient.sendSms(streamTelekomPushMsg);
        }, EMPTY_RUNNABLE, smsPushMessage.getNotificationId());
    }

    private boolean checkIfWebSocketConnected(Long userId) {
        return protocolHandler.isUserConnected(userId);
    }

    private void saveNotificationAndSetNotificationId(Notification notification) {
        var notificationDto = new NotificationsDto();
        notificationDto.setBody(notification.getBody());
        notificationDto.setTitle(notification.getTitle());
        notificationDto.setIsReaded(false);
        notificationDto.setSendedAt(new Date().getTime());
        notificationDto.setScreenId(notification.getScreen().getId());
        notificationDto.setType(notification.getType());
        notificationDto.setUserId(notification.getUserId());
        notificationDto.setMerchantId(notification.getMerchantId());
        NotificationsDto notificationResponse = kafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("notifications/save")
                .withRequestBody(notificationDto)
                .sendForEntity();
        notification.setNotificationId(notificationResponse.getId());
    }

    private NotificationResponseDto sendAndSetResponse(CheckedRunnable action, Runnable exceptionAction, Long msgId) {
        NotificationResponseDto response = new NotificationResponseDto();
        response.setMessageId(msgId);
        try {
            action.run();
            response.setStatus(MessageStatus.SENT);
        } catch (Exception e) {
            log.error("Error while sending notification message: ", e);
            exceptionAction.run();
            response.setStatus(MessageStatus.NOT_SENT);
        }
        return response;
    }
}
