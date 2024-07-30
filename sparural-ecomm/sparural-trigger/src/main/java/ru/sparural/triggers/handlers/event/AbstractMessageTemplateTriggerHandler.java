package ru.sparural.triggers.handlers.event;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import ru.sparural.triggers.model.MessageTemplateTrigger;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import ru.sparural.triggers.entities.JobDetailsTriggerEntity;
import ru.sparural.triggers.exceptions.ValidationException;
import ru.sparural.triggers.repositories.JobDetailsTriggerRepository;

import static ru.sparural.triggers.handlers.event.TimeUnits.*;

/**
 * @author Vorobyev Vyacheslav
 */
@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractMessageTemplateTriggerHandler {

    private DSLContext dslContext;

    private final JobDetailsTriggerRepository jobDetailsTriggerRepository;
    private SchedulerFactoryBean sfb;
    protected Map<TimeUnits, MessageFormat> messageFormatMap;

    /**
     *  | * | * |  * |     *     |   *  |     *    |
     *  |sec|min|hour|daysOfMonth|months|daysOfWeek|
     */
    @PostConstruct
    private void init() {
        messageFormatMap = Map.of(
                MINUTES,
                new MessageFormat("0 {1}/{0} {3}-{4} * * ?"),
                HOUR,
                new MessageFormat("0 {1} {3}-{4}/{0} * * ?"),
                DAYS,
                new MessageFormat("0 {1} {3}-{4} */{0} * ?"),
                WEEKS, // should be deprecated
                new MessageFormat("0 {1} {3} {4} * * ?"),
                MONTHS,
                new MessageFormat("0 {1} {3} {4} */{0} ?")
        );
    }

    private Long getFrequency(MessageTemplateTrigger trigger) {
         switch (trigger.getTimeUnit()) {
            case "M":
                if (trigger.getFrequency() > 12) {
                    throw new ValidationException("Month frequency must not be greater 12");
                }
                return trigger.getFrequency();
            case "w":
                if (trigger.getFrequency() > 7) {
                    throw new ValidationException("Month week must not be greater 7");
                }
                return 7 / trigger.getFrequency();
            default:
                return trigger.getFrequency();
        }
    }
    protected void handleEvent(Class<? extends Job> jobclass,
                            MessageTemplateTrigger trigger,
                            JobDataMap jobDataMap,
                            String jobIdentifier) {
        var qJobDetail = JobBuilder
                .newJob(jobclass)
                .usingJobData(jobDataMap)
                .withIdentity(new JobKey(jobIdentifier, trigger.getEventType().getEventTypeName()))
                .build();

        var timeUnit = TimeUnits.parseTimeUnit(trigger.getTimeUnit());

        // !!
        // For 'months' or 'weeks' frequency there is should be no
        // interval values in hours/minutes/seconds,
        // or it starts at every time in specified range
        int lastArg;

        if (timeUnit.equals(MONTHS) || timeUnit.equals(WEEKS)) {
            lastArg = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        } else {
            lastArg = trigger.getHoursEnd() == 0 ? 23 : trigger.getHoursEnd();
        }

        var args = new Object[]{
                getFrequency(trigger),
                trigger.getMinutesStart(),
                trigger.getMinutesEnd() == 0 ? 59 : trigger.getMinutesEnd(),
                trigger.getHoursStart(),
                lastArg
        };


        var mf = messageFormatMap.get(timeUnit);

        var format = mf.format(args);

        try {
            var cronExpression = new CronExpression(format);
            var qTrigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
                            .withMisfireHandlingInstructionDoNothing())
                    .startAt(trigger.getStartDate())
                    .endAt(trigger.getEndDate())
                    .build();
            log.info("Job created: {}",
                    trigger.getEventType().getEventTypeName());
            var nextFireTime = sfb.getScheduler().scheduleJob(qJobDetail, qTrigger);
            var jobDetailsTriggerEntity = new JobDetailsTriggerEntity();
            jobDetailsTriggerEntity.setTriggerDocumentId(trigger.getMessagesTemplate().getId());
            var jobKey = qJobDetail.getKey();
            jobDetailsTriggerEntity.setJobGroup(jobKey.getGroup());
            jobDetailsTriggerEntity.setJobName(jobKey.getName());
            var entity = jobDetailsTriggerRepository.create(jobDetailsTriggerEntity);
            log.info("Trigger created: " + entity +
                    "\n\t\t | Current time: " + new Date() +
                    "\n\t\t | Next fire time: " + nextFireTime);
        } catch (SchedulerException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setSfb(SchedulerFactoryBean sfb) {
        this.sfb = sfb;
    }

    @Autowired
    public void setDslContext(DSLContext dslContext) {
        this.dslContext = dslContext;
    }
}