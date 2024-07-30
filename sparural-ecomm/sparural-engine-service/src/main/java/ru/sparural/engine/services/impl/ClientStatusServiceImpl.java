package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.ClientStatusDto;
import ru.sparural.engine.entity.ClientStatusEntity;
import ru.sparural.engine.repositories.ClientStatusRepository;
import ru.sparural.engine.services.ClientStatusService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientStatusServiceImpl implements ClientStatusService {
    private final ClientStatusRepository clientStatusRepository;
    private final DtoMapperUtils mapperUtils;

    @Override
    public List<ClientStatusDto> list(int offset, int limit) {
        return createDtoList(clientStatusRepository.fetch(offset, limit));
    }

    @Override
    public ClientStatusDto get(Long id) {
        return createDto(clientStatusRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public ClientStatusDto createDto(ClientStatusEntity entity) {
        return mapperUtils.convert(ClientStatusDto.class, () -> entity);
    }

    @Override
    public List<ClientStatusDto> createDtoList(List<ClientStatusEntity> list) {
        return mapperUtils.convertList(ClientStatusDto.class, () -> list);
    }
}
