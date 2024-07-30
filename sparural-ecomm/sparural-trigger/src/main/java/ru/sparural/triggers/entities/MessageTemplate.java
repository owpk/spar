package ru.sparural.triggers.entities;

import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageTemplate implements Serializable {
    Long id;
    Long messageTypeId;
    String name;
    String subject;
    String message;
    String messageHtml;
    Long screenId;
    Long notificationTypeId;
    Boolean sendToEveryone;
    @Builder.Default
    Set<Long> users = new HashSet<>();
    @Builder.Default
    Set<Long> usersGroup = new HashSet<>();
    Boolean isSystem;
    Boolean required;
    Long triggerId;
    Integer lifetime;
    Long currencyId;
    Integer currencyDaysBeforeBurning;
    Integer daysWithoutPurchasing;
}