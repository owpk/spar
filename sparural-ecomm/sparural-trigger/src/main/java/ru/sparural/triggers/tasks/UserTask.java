package ru.sparural.triggers.tasks;

/**
 * @author Vorobyev Vyacheslav
 */

import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.triggerapi.dto.MessageTemplateDto;

public interface UserTask {
    void executeUserTask(MessageTemplateDto messageTemplateDto, UserFilterDto filter);
}
