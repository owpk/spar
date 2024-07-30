package ru.sparural.notification.service.impl.devino;

/**
 * @author Vorobyev Vyacheslav
 */
public final class DevinoConstants {
    public static final String BASE_ENDPOINT = "https://integrationapi.net";

    public static final String SEND_EMAIL_PUSH = appendToBaseUrl("/email/v2/messages");
    public static final String SEND_VIBER_PUSH = "https://viber.devinotele.com:444/send";
    public static final String SEND_WHATSAPP_PUSH = "https://im.devinotele.com/send/whatsapp";

    public static String appendToBaseUrl(String url) {
        return BASE_ENDPOINT + url;
    }

}