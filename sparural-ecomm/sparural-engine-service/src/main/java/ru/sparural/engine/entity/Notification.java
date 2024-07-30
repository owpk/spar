package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Notification {
    private EmailSetting email;
    private SmsSetting sms;
    private WhatsAppSetting whatsapp;
    private ViberSetting viber;
    private PushSetting push;
}
