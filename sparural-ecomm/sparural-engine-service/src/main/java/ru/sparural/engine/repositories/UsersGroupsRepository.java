package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.UserGroup;

import java.util.List;
import java.util.Optional;

public interface UsersGroupsRepository {
    List<UserGroup> list(int offset, int limit, String name);

    List<UserGroup> getByIds(List<Long> groupIds);

    Optional<UserGroup> get(Long id);

    UserGroup create(UserGroup data);

    UserGroup update(UserGroup data, Long id);

    boolean delete(Long id);

    UserGroup findById(Long id);
}
