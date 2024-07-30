package ru.sparural.notification.service.impl.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.notification.config.SettingsBeanFactory;
import ru.sparural.notification.service.impl.devino.dto.email.Sender;
import ru.sparural.notification.service.impl.devino.DevinoServiceRestClient;
import ru.sparural.notification.service.impl.devino.dto.email.Body;
import ru.sparural.notification.service.impl.devino.dto.email.DevinoEmailPushRequestDto;
import ru.sparural.notification.service.impl.devino.dto.email.MergeFields;
import ru.sparural.notification.service.impl.devino.dto.email.Recipient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final SettingsBeanFactory settingsBeanFactory;
    private final DevinoServiceRestClient devinoServiceRestClient;

    public void sendEmail(List<EmailRecipients> recipients,
                          EmailSenderInfo emailSenderInfo,
                          String subject, String content) {
        var notificationServiceConfigBean = settingsBeanFactory.getConfigBean();
        var settings = notificationServiceConfigBean.getEmailSettings();
        var devinoEmailPushRequestDto = new DevinoEmailPushRequestDto();
        var sender = new Sender();
        sender.setAddress(settings.getEmailSenderEmail());
        sender.setName(settings.getEmailSenderName());
        var body = new Body();
        body.setBody(content + "[Unsubscribe]");
        devinoEmailPushRequestDto.setBody(body);
        List<Recipient> recipient = new ArrayList<>();
        recipients.forEach(x -> recipient.add(
                new Recipient(new MergeFields(x.getName()), x.getAddress())));
        devinoEmailPushRequestDto.setRecipients(recipient);
        devinoEmailPushRequestDto.setSubject(subject);
        devinoEmailPushRequestDto.setCheckUnsubscription(true);
        devinoEmailPushRequestDto.setSender(sender);
        devinoServiceRestClient.sendEmail(devinoEmailPushRequestDto);
    }
}