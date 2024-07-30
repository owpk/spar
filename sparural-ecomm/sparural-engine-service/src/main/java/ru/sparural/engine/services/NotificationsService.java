package ru.sparural.engine.services;


import ru.sparural.engine.api.dto.NotificationsDto;
import ru.sparural.engine.api.dto.NotificationsListWithMetaDto;
import ru.sparural.engine.api.dto.templates.MessageTemplateDto;
import ru.sparural.engine.entity.NotificationsEntity;

import java.util.List;

public interface NotificationsService {
    NotificationsListWithMetaDto list(Long userId, Integer offset, Integer limit, Boolean isReaded, List<String> types);

    NotificationsDto update(long id, long userId);

    NotificationsDto createDto(NotificationsEntity entity);

    List<NotificationsDto> createDtoList(List<NotificationsEntity> entities);

    NotificationsEntity createEntity(NotificationsDto dto);

    Long create(MessageTemplateDto messageTemplateDto, Long userId);

    NotificationsEntity save(NotificationsEntity entity);
}
