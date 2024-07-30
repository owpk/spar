package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class SmsSetting {
    private String gatewayLogin;
    private String gatewayPassword;
    private String senderName;
    private int frequency;
}
