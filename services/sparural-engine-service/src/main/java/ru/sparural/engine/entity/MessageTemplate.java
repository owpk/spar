package ru.sparural.engine.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageTemplate {
    Long id;
    Long messageTypeId;
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
    Boolean required;
    Long triggerId;
    Integer lifetime;
}