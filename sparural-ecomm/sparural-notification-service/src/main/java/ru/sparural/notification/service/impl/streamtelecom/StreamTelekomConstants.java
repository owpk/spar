package ru.sparural.notification.service.impl.streamtelecom;

/**
 * @author Vorobyev Vyacheslav
 */
public class StreamTelekomConstants {
    private static final String BASE_API_PATH = "https://gateway.api.sc/rest";

    public static class Send {
        public static final String SEND_SINGLE = StreamTelekomConstants.BASE_API_PATH + "/Send/SendSms";
        public static final String SEND_BULK_PACKET = StreamTelekomConstants.BASE_API_PATH + "/Send/SendBulkPacket/";

        // REQUIRED PARAMS:
        public static final String PARAM_LOGIN = "login";
        public static final String PARAM_PASSWORD = "pass";
        public static final String PARAM_SOURCE_ADDRESS = "sourceAddress";
        public static final String PARAM_DESTINATION_ADDRESS = "destinationAddress";
        public static final String PARAM_DATA = "data";
        // phone_data={"sms": [{"phone":"79114567865","text":"text1"}] }
        public static final String PARAM_PHONE_DATA = "phone_data";
    }
}
