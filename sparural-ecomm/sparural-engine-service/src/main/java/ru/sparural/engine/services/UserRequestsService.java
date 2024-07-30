package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.UserRequestsDto;
import ru.sparural.engine.entity.UserRequestEntity;

import java.util.List;

public interface UserRequestsService {
    UserRequestsDto save(Long userId, UserRequestsDto userRequest);

    UserRequestsDto update(Long id, Long userId, UserRequestsDto userRequest);

    UserRequestsDto createDto(UserRequestEntity userRequest);

    UserRequestEntity createEntity(UserRequestsDto userRequestsDto);

    List<UserRequestsDto> createDtoList(List<UserRequestEntity> list);

    UserRequestsDto get(Long id);

    List<UserRequestsDto> list(int offset, int limit, String search);

    Boolean delete(Long id);
}
