package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NotificationSetting {
    private Boolean smsAllowed;
    private Boolean emailAllowed;
    private Boolean viberAllowed;
    private Boolean whatsappAllowed;
    private Boolean pushAllowed;
}
