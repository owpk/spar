package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.UserAttributesEntity;

import java.util.List;
import java.util.Optional;

public interface UserAttributesRepository {
    Optional<UserAttributesEntity> fetchById(Long id);

    List<UserAttributesEntity> list(Integer offset, Integer limit);

    Boolean delete(Long id);

    Optional<UserAttributesEntity> update(Long id, UserAttributesEntity entity);

    Optional<UserAttributesEntity> create(UserAttributesEntity data);

}
