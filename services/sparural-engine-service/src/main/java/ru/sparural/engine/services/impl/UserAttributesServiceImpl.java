package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.UserAttributesEntity;
import ru.sparural.engine.repositories.UserAttributesRepository;
import ru.sparural.engine.services.UserAttributesService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAttributesServiceImpl implements UserAttributesService {
    private final UserAttributesRepository userAttributesRepository;

    @Override
    public List<UserAttributesEntity> index(Integer offset, Integer limit) {
        return userAttributesRepository.list(offset, limit);
    }

    @Override
    public UserAttributesEntity get(Long id) {
        return userAttributesRepository.fetchById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.valueOf(id)));
    }

    @Override
    public UserAttributesEntity update(Long id, UserAttributesEntity data) {
        return userAttributesRepository.update(id, data)
                .orElseThrow(() -> new RuntimeException("Cannot update user attribute with id: " + id));
    }

    @Override
    public Boolean delete(Long id) {
        return userAttributesRepository.delete(id);
    }

    @Override
    public UserAttributesEntity create(UserAttributesEntity data) {
        return userAttributesRepository.create(data)
                .orElseThrow(() -> new RuntimeException("Cannot create user attribute"));
    }
}