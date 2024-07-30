package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.ClientStatusDto;
import ru.sparural.engine.entity.ClientStatusEntity;

import java.util.List;

public interface ClientStatusService {
    List<ClientStatusDto> list(int offset, int limit);

    ClientStatusDto get(Long id);

    ClientStatusDto createDto(ClientStatusEntity entity);

    List<ClientStatusDto> createDtoList(List<ClientStatusEntity> list);
}
