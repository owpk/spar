package ru.sparural.triggers.entities;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageTemplateUsersGroup {
    Long id;
    Long messageTemplate;
    Long usersGroupId;
}
