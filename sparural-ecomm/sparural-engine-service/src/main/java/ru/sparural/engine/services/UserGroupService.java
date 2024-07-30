package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.UserGroupDto;
import ru.sparural.engine.api.dto.UsersGroupUserDto;

import java.util.List;

public interface UserGroupService {
    List<UserGroupDto> list(int offset, int limit, String search);

    UserGroupDto get(Long id);

    List<UserGroupDto> getByIds(List<Long> id);

    UserGroupDto create(UserGroupDto group);

    UserGroupDto update(UserGroupDto group, Long id);

    boolean delete(Long id);

    boolean addUsers(UsersGroupUserDto usersGroupUserDto, Long groupId);

    boolean removeUsers(UsersGroupUserDto usersGroupUserDto, Long groupId);

    void addUserToGroupByCode(Long userId, Integer groupCode);

    List<Long> findUsersByGroupId(Long groupId);
}
