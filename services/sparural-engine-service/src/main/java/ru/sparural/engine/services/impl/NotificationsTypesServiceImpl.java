package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.NotificationsTypesDto;
import ru.sparural.engine.entity.NotificationsType;
import ru.sparural.engine.repositories.NotificationsTypesRepository;
import ru.sparural.engine.services.NotificationsTypesService;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationsTypesServiceImpl implements NotificationsTypesService {

    private final NotificationsTypesRepository repository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<NotificationsTypesDto> list(Integer offset, Integer limit) {
        return createDtoList(repository.list(offset, limit));
    }

    @Override
    public List<NotificationsTypesDto> createDtoList(List<NotificationsType> entities) {
        return dtoMapperUtils.convertList(NotificationsTypesDto.class, () -> entities);
    }

    @Override
    public NotificationsTypesDto get(Long id) {
        return dtoMapperUtils.convert(repository.get(id), NotificationsTypesDto.class);
    }
}
