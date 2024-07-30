package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmailSetting {
    private String devinoLogin;
    private String devinoPassword;
    private String senderEmail;
    private String senderName;
    private int frequency;
}
