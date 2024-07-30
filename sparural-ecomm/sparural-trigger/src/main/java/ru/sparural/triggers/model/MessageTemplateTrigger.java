package ru.sparural.triggers.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggers.entities.MessageTemplate;

import java.util.Date;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTemplateTrigger {
    Long id;
    Date startDate;
    Date endDate;
    Integer minutesStart;
    Integer minutesEnd;
    Integer hoursStart;
    Integer hoursEnd;
    Long frequency;
    String timeUnit;
    MessageTemplateDto messagesTemplate;
    EventType eventType;
}