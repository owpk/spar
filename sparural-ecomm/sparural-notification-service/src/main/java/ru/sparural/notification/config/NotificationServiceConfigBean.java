package ru.sparural.notification.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sparural.engine.api.dto.NotificationDto;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
public final class NotificationServiceConfigBean {
    public static final String DEFAULT_BEAN_NAME = "settings";
    private EmailSettings emailSettings;
    private SmsSettings smsSettings;
    private ViberSettings viberSettings;
    private WhatsAppSettings whatsAppSettings;
    private PushSettings pushSettings;
    private NotificationDto notificationDto;

    public NotificationServiceConfigBean(NotificationDto settingsDto) {
        this.notificationDto = settingsDto;
        var dtoEmail = settingsDto.getEmail();
        this.emailSettings = EmailSettings.builder()
                .emailDevinoLogin(dtoEmail.getDevinoLogin())
                .emailDevinoPassword(dtoEmail.getDevinoPassword())
                .emailSenderEmail(dtoEmail.getSenderEmail())
                .emailSenderName(dtoEmail.getSenderName())
                .build();
        var dtoPush = settingsDto.getPush();
        this.pushSettings = PushSettings.builder()
                .firebaseProjectId(dtoPush.getFirebaseProjectId())
                .huaweiAppId(dtoPush.getHuaweiAppId())
                .huaweiAppSecret(dtoPush.getHuaweiAppSecret())
                .build();
        var dtoSms = settingsDto.getSms();
        this.smsSettings = SmsSettings.builder()
                .smsGatewayPassword(dtoSms.getGatewayPassword())
                .smsGetewayLogin(dtoSms.getGatewayLogin())
                .smsSenderName(dtoSms.getSenderName())
                .build();
        var dtoViber = settingsDto.getViber();
        this.viberSettings = ViberSettings.builder()
                .viberDevinoLogin(dtoViber.getDevinoLogin())
                .viberDevinoPassword(dtoViber.getDevinoPassword())
                .viberSenderName(dtoViber.getSenderName())
                .build();
        var dtoWhatsapp = settingsDto.getWhatsapp();
        this.whatsAppSettings = WhatsAppSettings.builder()
                .whatsappDevinoLogin(dtoWhatsapp.getDevinoLogin())
                .whatsappDevinoPassword(dtoWhatsapp.getDevinoPassword())
                .whatsappSenderName(dtoWhatsapp.getSenderName())
                .build();
    }

    @Override
    public String toString() {
        return notificationDto.toString();
    }

    @Getter
    @Setter
    @Builder
    public static class EmailSettings {
        private String emailDevinoLogin;
        private String emailDevinoPassword;
        private String emailSenderName;
        private String emailSenderEmail;
    }

    @Getter
    @Setter
    @Builder
    public static class ViberSettings {
        private String viberDevinoLogin;
        private String viberDevinoPassword;
        private String viberSenderName;
    }

    @Getter
    @Setter
    @Builder
    public static class SmsSettings {
        private String smsGetewayLogin;
        private String smsGatewayPassword;
        private String smsSenderName;
    }

    @Getter
    @Setter
    @Builder
    public static class WhatsAppSettings {
        private String whatsappDevinoLogin;
        private String whatsappDevinoPassword;
        private String whatsappSenderName;
    }

    @Getter
    @Setter
    @Builder
    public static class PushSettings {
        private String firebaseProjectId;
        private String huaweiAppId;
        private String huaweiAppSecret;
    }
}
