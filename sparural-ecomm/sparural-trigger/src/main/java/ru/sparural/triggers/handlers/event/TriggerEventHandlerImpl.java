package ru.sparural.triggers.handlers.event;

import org.quartz.JobDataMap;
import org.springframework.stereotype.Component;
import ru.sparural.triggers.job.TriggerJob;
import ru.sparural.triggers.model.MessageTemplateTrigger;

import java.util.UUID;
import ru.sparural.triggers.repositories.JobDetailsTriggerRepository;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
public class TriggerEventHandlerImpl extends AbstractMessageTemplateTriggerHandler {

    public TriggerEventHandlerImpl(JobDetailsTriggerRepository jobDetailsTriggerRepository) {
        super(jobDetailsTriggerRepository);
    }

    public void handleEvent(MessageTemplateTrigger messageTemplateTrigger) {
        var jobData = new JobDataMap();
        jobData.put("msgTemplate", messageTemplateTrigger.getMessagesTemplate());
        jobData.put("eventType", messageTemplateTrigger.getEventType());
        super.handleEvent(TriggerJob.class, messageTemplateTrigger, jobData,
                messageTemplateTrigger.getEventType().getEventTypeName()
                + ":" + UUID.randomUUID());
    }

}
