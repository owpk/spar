package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.MerchantDto;
import ru.sparural.engine.api.dto.MetaForNotification;
import ru.sparural.engine.api.dto.NotificationFullDto;
import ru.sparural.engine.api.dto.NotificationsDto;
import ru.sparural.engine.api.dto.NotificationsListWithMetaDto;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.engine.api.dto.templates.MessageTemplateDto;
import ru.sparural.engine.entity.MerchantAttribute;
import ru.sparural.engine.entity.NotificationsEntity;
import ru.sparural.engine.entity.NotificationsFullEntity;
import ru.sparural.engine.repositories.NotificationsListRepository;
import ru.sparural.engine.services.NotificationsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationsListServiceImpl implements NotificationsService {
    private final NotificationsListRepository notificationsListRepository;
    private final ModelMapper modelMapper;

    @Override
    public NotificationsListWithMetaDto list(Long userId, Integer offset, Integer limit, Boolean isReaded, List<String> types) {
        var notifications = notificationsListRepository.fetch(userId, offset, limit, isReaded, types);
        var response = new NotificationsListWithMetaDto();
        response.setData(notifications.stream()
                .map(this::createDto)
                .collect(Collectors.toList()));
        response.setMeta(MetaForNotification.builder()
                .unread_messages_count(notificationsListRepository.getUnreadedMessagesCount(userId))
                .build());
        return response;
    }

    @Override
    public NotificationsDto update(long id, long userId) {
        return createDto(notificationsListRepository.get(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", 404)));
    }

    private NotificationFullDto createDto(NotificationsFullEntity entity) {
        var dto = new NotificationFullDto();
        dto.setBody(entity.getBody());
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setIsReaded(entity.getIsReaded());
        dto.setUserId(entity.getUserId());
        dto.setType(entity.getType());
        dto.setSendedAt(entity.getSendedAt());
        dto.setScreen(modelMapper.map(entity.getScreen(), ScreenDto.class));
        var merchantDto = new MerchantDto();
        var merchantEntity = entity.getMerchant();
        merchantDto.setId(merchantEntity.getId());
        merchantDto.setAddress(merchantEntity.getAddress());
        merchantDto.setTitle(merchantEntity.getTitle());
        merchantDto.setAttributes(merchantEntity.getAttributes() != null ?
                merchantEntity.getAttributes()
                        .stream().map(MerchantAttribute::getId).collect(Collectors.toList()) : Collections.emptyList());
        merchantDto.setWorkingHoursFrom(merchantEntity.getWorkingHoursFrom());
        merchantDto.setWorkingHoursTo(merchantEntity.getWorkingHoursTo());
        merchantDto.setFormatId(merchantEntity.getFormatId());
        merchantDto.setLatitude(merchantDto.getLatitude());
        merchantDto.setLongitude(merchantDto.getLongitude());
        merchantDto.setLoymaxLocationId(merchantEntity.getLoymaxLocationId());
        merchantDto.setWorkingStatus(merchantEntity.getWorkingStatus() != null ? merchantEntity.getWorkingStatus().getName() : "");
        dto.setMerchant(merchantDto);
        return dto;
    }

    @Override
    public NotificationsDto createDto(NotificationsEntity entity) {
        return NotificationsDto.builder()
                .id(entity.getId())
                .body(entity.getBody())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .sendedAt(entity.getSendedAt())
                .type(entity.getType())
                .isReaded(entity.getIsReaded())
                .screenId(entity.getScreenId())
                .build();
    }

    @Override
    public List<NotificationsDto> createDtoList(List<NotificationsEntity> entities) {
        return entities.stream()
                .map(entity -> NotificationsDto.builder()
                        .id(entity.getId())
                        .body(entity.getBody())
                        .userId(entity.getUserId())
                        .title(entity.getTitle())
                        .sendedAt(entity.getSendedAt())
                        .type(entity.getType())
                        .isReaded(entity.getIsReaded())
                        .screenId(entity.getScreenId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public NotificationsEntity createEntity(NotificationsDto dto) {
        NotificationsEntity entity = new NotificationsEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setBody(dto.getBody());
        entity.setTitle(dto.getTitle());
        entity.setType(dto.getType());
        entity.setIsReaded(dto.getIsReaded());
        entity.setSendedAt(dto.getSendedAt());
        entity.setScreenId(dto.getScreenId());
        return entity;
    }

    @Override
    public Long create(MessageTemplateDto messageTemplateDto, Long userId) {
        var notificationsEntity = new NotificationsEntity();
        notificationsEntity.setBody(messageTemplateDto.getMessage());
        notificationsEntity.setUserId(userId);
        notificationsEntity.setScreenId(Optional.ofNullable(messageTemplateDto.getScreen())
                .map(ScreenDto::getId)
                .orElse(null));
        notificationsEntity.setTitle(messageTemplateDto.getName());
        var saved = notificationsListRepository.save(notificationsEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot save notification message, no saved result present"));
        return saved.getId();
    }

    @Override
    public NotificationsEntity save(NotificationsEntity entity) {
        return notificationsListRepository.save(entity)
                .orElseThrow(() -> new ResourceNotFoundException("Can't save entity: " + entity));
    }

}