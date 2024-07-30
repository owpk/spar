package ru.sparural.notification.service.impl.streamtelecom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sparural.notification.config.SettingsBeanFactory;
import ru.sparural.notification.service.impl.streamtelecom.dto.PhoneData;
import ru.sparural.notification.service.impl.streamtelecom.dto.StreamTelekomSms;
import ru.sparural.notification.service.impl.streamtelecom.dto.StreamTelekomSmsPushDto;
import ru.sparural.utils.RestTemplateConstants;
import ru.sparural.utils.rest.RestTemplate;

import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
public class StreamTelecomRestClient {

    private final RestTemplate restTemplate;
    private final SettingsBeanFactory settingsBeanFactory;

    // login - Логин, присвоенный Клиенту. Обязательный.
    // pass - Пароль API, присвоенный Клиенту. Обязательный.
    // destinationAddress - Номера получателя сообщения, в международном формате: код страны + код сети + номер телефона. Пример: 79031234567. Обязательный.
    // sourceAddress Имя отправителя. До 11 латинских символов или до 15 цифровых. Примечание: Передаваемое значение в адресе отправителя, должно в точности соответствовать ранее зарегистрированному.
    // data - Текст сообщения. Обязательный.
    public void sendSms(StreamTelekomSmsPushDto smsPushMessage) {
        var configBean = settingsBeanFactory.getConfigBean();
        var settings = configBean.getSmsSettings();
        var phoneData = new PhoneData();
        phoneData.setSms(smsPushMessage.getMessages().stream().map(x -> {
            var sms = new StreamTelekomSms();
            sms.setPhone(x.getDestinationAddress());
            sms.setText(x.getData());
            return sms;
        }).collect(Collectors.toList()));
        var phoneDataString = restTemplate.getJsonConverter().writeValueToString(phoneData);
        var body = appendPath(StreamTelekomConstants.Send.PARAM_LOGIN, settings.getSmsGetewayLogin(), true) +
                appendPath(StreamTelekomConstants.Send.PARAM_PASSWORD, settings.getSmsGatewayPassword(), false) +
                appendPath(StreamTelekomConstants.Send.PARAM_SOURCE_ADDRESS, settings.getSmsSenderName(), false) +
                appendPath(StreamTelekomConstants.Send.PARAM_PHONE_DATA, phoneDataString, false);
        restTemplate.request()
                .addHeader(RestTemplateConstants.HeadersKeys.CONTENT_TYPE,
                        RestTemplateConstants.MediaTypes.APPLICATION_FORM_URLENCODED)
                .post(StreamTelekomConstants.Send.SEND_BULK_PACKET, body);
    }

    private String appendPath(String key, String value, boolean start) {
        return (start ? "" : "&") + key + "=" + value;
    }
}
