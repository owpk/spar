package ru.sparural.notification.service.impl.devino;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sparural.notification.config.SettingsBeanFactory;
import ru.sparural.notification.model.viber.ViberPushMessage;
import ru.sparural.notification.model.whatsapp.WhatsAppPushMessage;
import ru.sparural.notification.service.impl.devino.dto.email.DevinoEmailPushRequestDto;
import ru.sparural.notification.service.impl.devino.dto.email.DevinoEmailResponseDto;
import ru.sparural.notification.service.impl.devino.dto.viber.DevinoViberPushDto;
import ru.sparural.notification.service.impl.devino.dto.whatsapp.Content;
import ru.sparural.notification.service.impl.devino.dto.whatsapp.DevinoWhatsAppPushDto;
import ru.sparural.notification.service.impl.devino.dto.whatsapp.Message;
import ru.sparural.utils.rest.AuthType;
import ru.sparural.utils.rest.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
public class DevinoServiceRestClient {

    private final SettingsBeanFactory settingsBeanFactory;
    private final RestTemplate restTemplate;

    public DevinoEmailResponseDto sendEmail(DevinoEmailPushRequestDto pushRequestDto) {
        var configBean = settingsBeanFactory.getConfigBean();
        var emailSettings = configBean.getEmailSettings();
        return restTemplate.request()
                .addHeader("Accept", "*/*")
                .withAuthorizationHeader(AuthType.BASIC,
                        encodeCredentials(emailSettings.getEmailDevinoLogin(), emailSettings.getEmailDevinoPassword()))
                .setResponseType(DevinoEmailResponseDto.class)
                .postForEntity(DevinoConstants.SEND_EMAIL_PUSH, pushRequestDto)
                .orElseThrow();
    }

    public void sendViber(ViberPushMessage viberPushMessage) {
        var configBean = settingsBeanFactory.getConfigBean();
        var settings = configBean.getViberSettings();
        var requestDto = new DevinoViberPushDto();
        requestDto.setResendSms(false);
        requestDto.setMessages(viberPushMessage
                .getMessages().stream()
                .map(x -> {
                    var msg = new Message();
                    msg.setAddress(x.getTo());
                    msg.setSmsSrcAddress("");
                    msg.setSubject("");
                    return msg;
                })
                .collect(Collectors.toList()));
        restTemplate.request()
                .addHeader("Accept", "*/*")
                .withAuthorizationHeader(AuthType.BASIC,
                        encodeCredentials(settings.getViberDevinoLogin(), settings.getViberDevinoPassword()))
                .post(DevinoConstants.SEND_VIBER_PUSH, requestDto);
    }

    public void sendWhatsApp(WhatsAppPushMessage whatsAppPushMessage) {
        var configBean = settingsBeanFactory.getConfigBean();
        var settings = configBean.getWhatsAppSettings();
        var requestDto = new DevinoWhatsAppPushDto();
        requestDto.setResendSms(false);
        requestDto.setMessages(whatsAppPushMessage
                .getMessages().stream()
                .map(x -> {
                    var msg = new Message();
                    msg.setAddress(x.getTo());
                    msg.setSmsSrcAddress("");
                    var content = new Content();
                    content.setText(x.getText());
                    msg.setContent(content);
                    msg.setSubject("");
                    return msg;
                })
                .collect(Collectors.toList()));
        restTemplate.request()
                .addHeader("Accept", "*/*")
                .withAuthorizationHeader(AuthType.BASIC,
                        encodeCredentials(settings.getWhatsappDevinoLogin(), settings.getWhatsappDevinoPassword()))
                .post(DevinoConstants.SEND_WHATSAPP_PUSH, requestDto);
    }


    private String encodeCredentials(String login, String secret) {
        var auth = String.format("%s:%s", login, secret)
                .getBytes(StandardCharsets.UTF_8);
        return new String(Base64.getEncoder()
                .encode(auth));
    }

}