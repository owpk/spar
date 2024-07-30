package ru.sparural.engine.repositories;

import java.util.List;

public interface UsersGroupUserRepository {

    boolean addUsersToGroup(List<Long> users, Long groupId);

    boolean deleteUsersFromGroup(List<Long> users, Long groupId);

    void addUserToGroup(Long userId, Integer groupCode);

    List<Long> findUsersByGroupId(Long groupId);
}
