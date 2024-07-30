package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ViberSetting {
    private int frequency;
    private String devinoPassword;
    private String devinoLogin;
    private String senderName;
}
