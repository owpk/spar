package ru.sparural.triggers.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggerapi.dto.MessageTemplateRequestDto;
import ru.sparural.triggerapi.dto.TriggerDto;
import ru.sparural.triggers.entities.MessageTemplate;
import ru.sparural.triggers.entities.MessageType;
import ru.sparural.triggers.entities.TriggerDocument;
import ru.sparural.triggers.exceptions.ResourceNotFoundException;
import ru.sparural.triggers.exceptions.ValidationException;
import ru.sparural.triggers.handlers.event.TriggerEventHandlerImpl;
import ru.sparural.triggers.model.EventType;
import ru.sparural.triggers.model.MessageTemplateTrigger;
import ru.sparural.triggers.repositories.JobDetailsTriggerRepository;
import ru.sparural.triggers.repositories.MessageTemplateRepository;
import ru.sparural.triggers.repositories.MessageTypeRepository;
import ru.sparural.triggers.repositories.TriggersDocumentTypesService;
import ru.sparural.triggers.services.TriggersDocumentsService;
import ru.sparural.triggers.utils.DtoMapperUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static ru.sparural.Tables.JOB_DETAILS_TRIGGERS;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageTemplateServiceImpl implements ru.sparural.triggers.services.MessageTemplateService {
    private final DtoMapperUtils mapperUtils;
    private final MessageTemplateRepository messageTemplateRepository;
    private final MessageTypeRepository messageTypeRepository;
    private final TriggersDocumentsService triggersDocumentsService;
    private final TriggersDocumentTypesService triggersDocumentTypesService;
    private final String MESSAGE_TEMPLATE_NAME = "messagesTemplate";
    private final SchedulerFactoryBean sfb;
    private final JobDetailsTriggerRepository jobDetailsTriggerRepository;

    private final TriggerEventHandlerImpl triggerEventHandler;

    @Override
    public List<MessageTemplateDto> list(Integer offset, Integer limit, String messageType) {
        List<String> messageTypes = List.of("push", "sms", "email", "viber", "whatsapp");
        if (!messageTypes.contains(messageType))
            throw new ValidationException("The type of message must be push, sms, email, viber or whatsapp");

        List<MessageTemplate> messageTemplates = messageTemplateRepository.list(offset, limit, messageType);
        List<MessageTemplateDto> messageTemplateDtoList = new ArrayList<>();
        messageTemplates.forEach(entity -> {
            MessageTemplateDto messageTemplateDto = this.createDto(entity);
            messageTemplateDtoList.add(messageTemplateDto);
        });
        return messageTemplateDtoList;
    }

    @Override
    public MessageTemplateDto get(Long id) {
        var entity = messageTemplateRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template of message not found"));
        return createDto(entity);
    }

    @Override
    @Transactional
    public MessageTemplateDto create(MessageTemplateRequestDto messageTemplateDto) {
        var entity = createEntity(messageTemplateDto);
        var msgTemplateEntity = messageTemplateRepository.create(entity)
                .orElseThrow(() -> new ResourceNotFoundException("Template of message not found"));
        TriggerDocument triggerDocument = mapperUtils.convert(messageTemplateDto.getTrigger(), TriggerDocument.class);
        triggerDocument.setTriggersDocumentTypeId(triggersDocumentTypesService.findIdByName(MESSAGE_TEMPLATE_NAME));
        triggerDocument.setTriggersDocumentId(msgTemplateEntity.getId());
        var triggerDto = triggersDocumentsService.create(triggerDocument);

        MessageTemplateDto templateDto = createDto(msgTemplateEntity);
        templateDto.setTrigger(triggerDto);

        initTrigger(triggerDto, templateDto);
        return templateDto;
    }

    @Override
    @Transactional
    public MessageTemplateDto update(Long id, MessageTemplateRequestDto messageTemplateDto) {
        cancelTasks(id);
        var entity = createEntity(messageTemplateDto);

        var msgTemplateEntity = messageTemplateRepository.update(id, entity)
                .orElseThrow(() -> new ResourceNotFoundException("Template of message not found"));

        TriggerDocument triggerDocument = mapperUtils.convert(messageTemplateDto.getTrigger(), TriggerDocument.class);
        triggerDocument.setTriggersDocumentTypeId(triggersDocumentTypesService.findIdByName(MESSAGE_TEMPLATE_NAME));
        triggerDocument.setTriggersDocumentId(msgTemplateEntity.getId());

        var triggerEntity = triggersDocumentsService.findByMessageTemplateId(id);
        var triggerDto = triggersDocumentsService.update(triggerDocument, triggerEntity.getId());

        MessageTemplateDto templateDto = createDto(msgTemplateEntity);
        templateDto.setTrigger(triggerDto);

        initTrigger(triggerDto, templateDto);
        return templateDto;
    }

    private void initTrigger(TriggerDto triggerDto, MessageTemplateDto templateDto) {
        var eventType = EventType
                .of(triggerDto.getTriggerType().getCode());

        var trigger = createTrigger(
                triggerDto.getId(),
                triggerDto.getTimeStart(),
                triggerDto.getTimeEnd(),
                new Date(triggerDto.getDateStart()),
                new Date(triggerDto.getDateEnd()),
                triggerDto.getFrequency(),
                triggerDto.getTimeUnit(),
                eventType,
                templateDto);

        initTriggerEvent(trigger);
    }

    @Async
    public void initTriggerEvent(MessageTemplateTrigger messageTemplateTrigger) {
        triggerEventHandler.handleEvent(messageTemplateTrigger);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        cancelTasks(id);
        messageTemplateRepository.delete(id);
        return true;
    }

    private void cancelTasks(Long msgTemplateId) {
        var jobDetailsTriggerEntities = jobDetailsTriggerRepository.getByTriggerDocumentId(msgTemplateId);
        var jobKeys = jobDetailsTriggerEntities.stream()
                .map(job -> new JobKey(job.getJobName(), job.getJobGroup()))
                .collect(Collectors.toList());

        try {
            sfb.getScheduler().deleteJobs(jobKeys);
            jobDetailsTriggerRepository.deleteByTriggerDocumentId(msgTemplateId);
        } catch (SchedulerException ex) {
            throw new RuntimeException(
                String.format("Cannot delete jobs: '%s'", jobKeys.stream()
                    .map(e -> String.format("Key: %s, Group: %s", e.getName(), e.getGroup()))
                    .collect(Collectors.joining("; ", "{", "}"))
                ),
                ex
            );
        }
    }

    @Override
    public List<MessageTemplateDto> createListDto(List<MessageTemplate> listEntity) {
        List<MessageTemplateDto> messageTemplateDtoList = new ArrayList<>();
        listEntity.forEach(entity -> messageTemplateDtoList.add(createDto(entity)));
        return messageTemplateDtoList;
    }

    @Override
    public MessageTemplate createEntity(MessageTemplateRequestDto dto) {
        MessageType messageType = messageTypeRepository.findByName(dto.getMessageType())
                .orElseThrow(() -> new ResourceNotFoundException("Type of message with this name not found"));

        return MessageTemplate.builder()
                .message(dto.getMessage())
                .messageHtml(dto.getMessageHTML())
                .messageTypeId(messageType.getId())
                .name(dto.getName())
                .subject(dto.getSubject())
                .screenId(dto.getScreenId())
                .notificationTypeId(dto.getNotificationTypeId())
                .sendToEveryone(dto.getSendToEveryone())
                .users(new HashSet<>(dto.getUsers()))
                .usersGroup(new HashSet<>(dto.getUsersGroup()))
                .required(dto.getRequred())
                .isSystem(dto.getIsSystem())
                .lifetime(dto.getLifetime())
                .daysWithoutPurchasing(dto.getDaysWithoutPurchasing())
                .currencyDaysBeforeBurning(dto.getCurrencyDaysBeforeBurning())
                .currencyId(dto.getCurrencyId())
                .build();
    }

    @Override
    public MessageTemplate getByUserId(long userId) {
        return messageTemplateRepository.getByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Template of message not found"));
    }

    @Override
    public MessageTemplateDto createDto(MessageTemplate entity) {
        var messageTemplateDto = MessageTemplateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .message(entity.getMessage())
                .subject(entity.getSubject())
                .messageHTML(entity.getMessageHtml())
                .sendToEveryone(entity.getSendToEveryone())
                .isSystem(entity.getIsSystem())
                .requred(entity.getRequired())
                .lifetime(entity.getLifetime())
                .currencyId(entity.getCurrencyId())
                .currencyDaysBeforeBurning(entity.getCurrencyDaysBeforeBurning())
                .daysWithoutPurchasing(entity.getDaysWithoutPurchasing())
                .build();

        MessageType messageType = messageTypeRepository.get(entity.getMessageTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Type of message not found"));
        messageTemplateDto.setMessageType(messageType.getName());
        messageTemplateDto.setScreenId(entity.getScreenId());
        messageTemplateDto.setNotificationTypeId(entity.getNotificationTypeId());
        messageTemplateDto.setUsers(entity.getUsers() != null ? new ArrayList<>(entity.getUsers()) : new ArrayList<>());
        messageTemplateDto.setUsersGroup(entity.getUsersGroup() != null ? new ArrayList<>(entity.getUsersGroup()) : new ArrayList<>());
        messageTemplateDto.setScreenId(entity.getScreenId());
        messageTemplateDto.setNotificationTypeId(entity.getNotificationTypeId());
        try {
            var triggerDto = triggersDocumentsService.findByIdAndNameMessageTemplate(MESSAGE_TEMPLATE_NAME, entity.getId());
            messageTemplateDto.setTrigger(triggerDto);
        } catch (Exception e) {
            messageTemplateDto.setTrigger(new TriggerDto());
        }
        return messageTemplateDto;
    }

    public MessageTemplateTrigger createTrigger(
            Long id,
            String startTime,
            String endTime,
            Date startDate,
            Date endDate,
            Integer frequency,
            String timeUnit,
            EventType eventType,
            MessageTemplateDto messageTemplate) {

        var trigger = new MessageTemplateTrigger();
        trigger.setId(id);
        trigger.setFrequency(Long.valueOf(frequency));
        trigger.setStartDate(startDate);
        trigger.setEndDate(endDate);
        trigger.setTimeUnit(timeUnit);
        trigger.setMessagesTemplate(messageTemplate);
        trigger.setEventType(eventType);

        var timeStart = startTime.split(":");
        var timeEnd = endTime.split(":");

        trigger.setMinutesStart(Integer.valueOf(timeStart[1]));
        trigger.setHoursStart(Integer.valueOf(timeStart[0]));
        trigger.setMinutesEnd(Integer.valueOf(timeEnd[1]));
        trigger.setHoursEnd(Integer.valueOf(timeEnd[0]));
        return trigger;
    }

}