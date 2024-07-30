package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageTemplateUsersGroup {
    Long id;
    Long messageTemplate;
    Long usersGroupId;
}
