package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.UserRequestsSubjectsDto;
import ru.sparural.engine.entity.UserRequestsSubject;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

public interface UserRequestsSubjectsService {
    List<UserRequestsSubject> getList(int offset, int limit);

    UserRequestsSubjectsDto create(UserRequestsSubjectsDto userRequestsSubjectsDto);

    UserRequestsSubjectsDto createDTOFromEntity(UserRequestsSubject userRequestsSubject);

    UserRequestsSubject createEntityFromDTO(UserRequestsSubjectsDto userRequestsSubjectsDto);

    Boolean delete(Long id) throws ResourceNotFoundException;

    UserRequestsSubjectsDto get(Long id) throws ResourceNotFoundException;

    UserRequestsSubjectsDto update(Long id, UserRequestsSubjectsDto userRequestsSubjectsDto) throws ResourceNotFoundException;

    UserRequestsSubjectsDto getWithoutEx(Long id);
}
