package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.NotificationsTypesDto;
import ru.sparural.engine.entity.NotificationsType;

import java.util.List;

public interface NotificationsTypesService {
    List<NotificationsTypesDto> list(Integer offset, Integer limit);

    List<NotificationsTypesDto> createDtoList(List<NotificationsType> entities);

    NotificationsTypesDto get(Long id);
}
