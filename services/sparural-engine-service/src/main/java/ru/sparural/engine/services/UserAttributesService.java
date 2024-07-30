package ru.sparural.engine.services;

import ru.sparural.engine.entity.UserAttributesEntity;

import java.util.List;

public interface UserAttributesService {
    List<UserAttributesEntity> index(Integer offset, Integer limit);

    UserAttributesEntity get(Long id);

    UserAttributesEntity update(Long id, UserAttributesEntity data);

    Boolean delete(Long id);

    UserAttributesEntity create(UserAttributesEntity data);
}
