package ru.sparural.triggers.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggers.handlers.task.UserTasksHandler;
import ru.sparural.triggers.model.EventType;
import lombok.Setter;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.enums.UserFilterRegistrationTypes;

/**
 * @author Vorobyev Vyacheslav
 */
@Setter
public class TriggerJob implements Job {

    @Autowired
    private UserTasksHandler userTasksHandler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var messageTemplate = (MessageTemplateDto) context.getJobDetail().getJobDataMap().get("msgTemplate");
        var eventType = (EventType) context.getJobDetail().getJobDataMap().get("eventType");

        UserFilterDto filter = new UserFilterDto();

        if (!Boolean.TRUE.equals(messageTemplate.getSendToEveryone())) {
            filter.setUserIds(messageTemplate.getUsers());
            filter.setGroupIds(messageTemplate.getUsersGroup());
        }

        filter.setRegistrationType(UserFilterRegistrationTypes.ALL);
        userTasksHandler.handleTask(filter, eventType, messageTemplate);
    }
}
