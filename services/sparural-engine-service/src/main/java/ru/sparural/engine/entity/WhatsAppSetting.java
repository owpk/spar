package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WhatsAppSetting {
    private int frequency;
    private String devinoLogin;
    private String devinoPassword;
    private String senderName;
}