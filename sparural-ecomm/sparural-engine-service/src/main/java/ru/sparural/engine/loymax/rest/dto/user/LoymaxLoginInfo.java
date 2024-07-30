package ru.sparural.engine.loymax.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxLoginInfo {
    String currentValue; // <!--Номер телефона.-->
    String attachDateTime; // 2019-12-05T00:00:00Z, <!--Дата прикрепления номера телефона.-->
    String attachType; // Site, <!--Способ привязки номера телефона: Site — через сайт, Sms — с помощью SMS, Operator — через Контакт-центр, MobileClient — через Мобильное приложение.-->
    Boolean isVerified; // true <!--Верификация номера телефона (true — пройдена успешно, false — не
}
