package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Role;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RolesRepository {
    List<Role> getListByUserIDs(Long userIDs);

    List<Role> getListByNames(List<String> names);

    Map<Long, List<Role>> getListByUserIDs(List<Long> userID);

    void deleteRoleForUser(Role role, Long userId);

    void addRoleForUser(Role role, Long userId);

    Optional<Role> getByName(String name);

    List<Role> getAll();
}
