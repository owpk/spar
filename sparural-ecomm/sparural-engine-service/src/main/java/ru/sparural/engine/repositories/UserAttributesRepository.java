package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.UserAttributesEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserAttributesRepository {
    Optional<UserAttributesEntity> fetchById(Long id);

    List<UserAttributesEntity> list(Integer offset, Integer limit);

    Boolean delete(Long id);

    Optional<UserAttributesEntity> update(Long id, UserAttributesEntity entity);

    Optional<UserAttributesEntity> create(UserAttributesEntity data);

    void batchBind(Map<Long, Set<Long>> attributesToBind);

    void deleteAllByUserIds(Set<Long> longs);

    Map<Long, Set<Long>> fetchAllByUserIds(Set<Long> longs);

    List<UserAttributesEntity> batchSaveUserAttributes(List<UserAttributesEntity> entitiesToSave);

    void deleteAllByUserIdAttributeId(Map<Long, Set<Long>> longs);
}
