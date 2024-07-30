package ru.sparural.notification.service.impl.huawei;

/**
 * @author Vorobyev Vyacheslav
 */
public class HuaweiRestConstants {
    public static final String HUAWEI_PUSH_BASE_URL = "https://push-api.cloud.huawei.com";
    public static final String HUAWEI_TOKEN = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";
    public static final String SEND_PUSH = appendToBaseUrl("/v1/{0}/messages:send");

    public static String appendToBaseUrl(String url) {
        return HUAWEI_PUSH_BASE_URL + url;
    }
}
