package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.UserRequestsSubjectsDto;
import ru.sparural.engine.entity.UserRequestsSubject;
import ru.sparural.engine.repositories.UserRequestsSubjectsRepository;
import ru.sparural.engine.services.UserRequestsSubjectsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserRequestsSubjectsServiceImpl implements UserRequestsSubjectsService {
    private final UserRequestsSubjectsRepository userRequestsSubjectsRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<UserRequestsSubject> getList(int offset, int limit) {
        List<UserRequestsSubject> userRequestsSubjectList = userRequestsSubjectsRepository.getList(offset, limit);
        if (userRequestsSubjectList.size() > 0) {
            return userRequestsSubjectList;
        } else {
            throw new ResourceNotFoundException("Resource not found", 404);
        }
    }

    @Override
    public UserRequestsSubjectsDto create(UserRequestsSubjectsDto userRequestsSubjectsDto) {
        return createDTOFromEntity(userRequestsSubjectsRepository
                .create(createEntityFromDTO(userRequestsSubjectsDto))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", 404)));
    }

    @Override
    public UserRequestsSubjectsDto createDTOFromEntity(UserRequestsSubject userRequestsSubject) {
        return dtoMapperUtils.convert(userRequestsSubject, UserRequestsSubjectsDto.class);
    }

    @Override
    public UserRequestsSubject createEntityFromDTO(UserRequestsSubjectsDto userRequestsSubjectsDto) {
        return dtoMapperUtils.convert(userRequestsSubjectsDto, UserRequestsSubject.class);
    }

    @Override
    public Boolean delete(Long id) {
        userRequestsSubjectsRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found", 404));
        return userRequestsSubjectsRepository.delete(id);
    }

    @Override
    public UserRequestsSubjectsDto get(Long id) throws ResourceNotFoundException {
        return createDTOFromEntity(userRequestsSubjectsRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found", 404)));
    }

    @Override
    public UserRequestsSubjectsDto update(Long id, UserRequestsSubjectsDto userRequestsSubjectsDto) throws ResourceNotFoundException {
        return createDTOFromEntity(userRequestsSubjectsRepository.update(id, createEntityFromDTO(userRequestsSubjectsDto))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", 404)));
    }

    @Override
    public UserRequestsSubjectsDto getWithoutEx(Long id) {
        UserRequestsSubject entity = userRequestsSubjectsRepository.get(id).orElse(null);
        if (entity != null) {
            return createDTOFromEntity(entity);
        }
        return null;
    }
}
