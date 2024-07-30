package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.UserRequestsDto;
import ru.sparural.engine.entity.UserRequestEntity;
import ru.sparural.engine.repositories.UserRequestsRepository;
import ru.sparural.engine.repositories.UserRequestsSubjectsRepository;
import ru.sparural.engine.services.UserRequestsService;
import ru.sparural.engine.services.exception.NotDraftException;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRequestsServiceImpl implements UserRequestsService {
    private final UserRequestsRepository userRequestsRepository;
    private final UserRequestsSubjectsRepository userRequestsSubjectsRepository;
    private final DtoMapperUtils mapperUtils;

    @Override
    public UserRequestsDto save(Long userId, UserRequestsDto userRequestsDto) {
        UserRequestEntity userRequest = createEntity(userRequestsDto);
        if (userRequestsDto.getSubjectId() != null && userRequest.getSubjectId() == 0) {
            userRequest.setSubjectId(null);
        }
        if (userRequest.getSubjectId() != null && !userRequestsSubjectsRepository.checkIfUserRequestsSubjectsExistsWithId(userRequest.getSubjectId())) {
            throw new ValidationException("Topic with this number not found");
        }
        return createDto(userRequestsRepository
                .create(userId, userRequest)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", 404)));
    }

    @Override
    public UserRequestsDto update(Long id, Long userId, UserRequestsDto userRequestDto) {
        if (userRequestsRepository.findUserRequestIsNotDraft(id)) {
            throw new NotDraftException("You can not change this appeal");
        }
        if (userRequestDto.getSubjectId() != null && userRequestDto.getSubjectId() == 0) {
            userRequestDto.setSubjectId(null);
        }
        if (userRequestDto.getSubjectId() != null) {
            if (!userRequestsSubjectsRepository.checkIfUserRequestsSubjectsExistsWithId(userRequestDto.getSubjectId())) {
                throw new ValidationException("Topic with this number not found");
            }
        }
        UserRequestEntity userRequest = createEntity(userRequestDto);
        return createDto(userRequestsRepository
                .update(id, userId, userRequest)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", 404)));
    }

    @Override
    public UserRequestsDto createDto(UserRequestEntity userRequestEntity) {
        var dto = mapperUtils.convert(userRequestEntity, UserRequestsDto.class);
        dto.setAttachments(new ArrayList<FileDto>());
        return dto;
    }

    @Override
    public UserRequestEntity createEntity(UserRequestsDto userRequestsDto) {
        return mapperUtils.convert(userRequestsDto, UserRequestEntity.class);
    }

    @Override
    public List<UserRequestsDto> createDtoList(List<UserRequestEntity> list) {
        return mapperUtils.convertList(UserRequestsDto.class, list);
    }

    @Override
    public UserRequestsDto get(Long id) {
        return createDto(userRequestsRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found", 404)));
    }

    @Override
    public List<UserRequestsDto> list(int offset, int limit, String search) {
        List<Long> subjectsIds = userRequestsSubjectsRepository.findIdsByName(search);
        return createDtoList(userRequestsRepository.index(offset, limit, search, subjectsIds));
    }

    @Override
    public Boolean delete(Long id) {
        return userRequestsRepository.delete(id);
    }


}
