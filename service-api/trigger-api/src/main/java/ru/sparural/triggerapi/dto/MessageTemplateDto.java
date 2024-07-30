package ru.sparural.triggerapi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTemplateDto implements Serializable {
    Long id;
    String messageType;
    String name;
    String subject;
    String message;
    String messageHTML;
    Long screenId;
    Long notificationTypeId;
    Boolean sendToEveryone;
    List<Long> users;
    List<Long> usersGroup;
    Boolean isSystem;
    @Builder.Default
    Boolean requred = false;
    TriggerDto trigger;
    Integer lifetime;
    Long currencyId;
    Integer currencyDaysBeforeBurning;
    Integer daysWithoutPurchasing;
}