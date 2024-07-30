package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageTemplateUser {
    Long id;
    Long messageTemplateId;
    Long userId;
}
