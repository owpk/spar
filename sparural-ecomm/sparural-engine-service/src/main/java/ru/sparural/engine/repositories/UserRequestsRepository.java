package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.UserRequestEntity;

import java.util.List;
import java.util.Optional;

public interface UserRequestsRepository {
    Optional<UserRequestEntity> create(Long userId, UserRequestEntity userRequest);

    Optional<UserRequestEntity> update(Long id, Long userId, UserRequestEntity userRequest);

    boolean findUserRequestIsNotDraft(Long id);

    Optional<UserRequestEntity> get(Long id);

    List<UserRequestEntity> index(int offset, int limit, String search, List<Long> subjectsIds);

    Boolean delete(Long id);
}
