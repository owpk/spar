package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.UserGroupDto;
import ru.sparural.engine.api.dto.UsersGroupUserDto;
import ru.sparural.engine.entity.UserGroup;
import ru.sparural.engine.repositories.UsersGroupUserRepository;
import ru.sparural.engine.repositories.UsersGroupsRepository;
import ru.sparural.engine.services.UserGroupService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {

    private final UsersGroupsRepository usersGroupsRepository;
    private final UsersGroupUserRepository usersGroupUserRepository;
    private final DtoMapperUtils dtoMapperUtils;

    private List<UserGroupDto> userGroupDtoFromUserGroup(List<UserGroup> user) {
        return dtoMapperUtils.convertList(UserGroupDto.class, user);
    }

    private UserGroupDto userGroupDtoFromUserGroup(UserGroup user) {
        return dtoMapperUtils.convert(user, UserGroupDto.class);
    }

    @Override
    public List<UserGroupDto> list(int offset, int limit, String search) {
        return UserGroupServiceImpl.this.userGroupDtoFromUserGroup(usersGroupsRepository.list(offset, limit, search));
    }

    @Override
    public UserGroupDto get(Long id) {
        return userGroupDtoFromUserGroup(usersGroupsRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена")));
    }

    @Override
    public UserGroupDto create(UserGroupDto group) {
        return userGroupDtoFromUserGroup(usersGroupsRepository.create(dtoMapperUtils.convert(group, UserGroup.class)));
    }

    @Override
    public UserGroupDto update(UserGroupDto group, Long id) {
        return userGroupDtoFromUserGroup(usersGroupsRepository.update(dtoMapperUtils.convert(group, UserGroup.class), id));
    }

    @Override
    public boolean delete(Long id) {
        return usersGroupsRepository.delete(id);
    }

    @Override
    public boolean addUsers(UsersGroupUserDto usersGroupUserDto, Long groupId) {
        List<Long> usersIds = usersGroupUserDto.getUsers();
        return usersGroupUserRepository.addUsersToGroup(usersIds, groupId);
    }

    @Override
    public boolean removeUsers(UsersGroupUserDto usersGroupUserDto, Long groupId) {
        List<Long> usersIds = usersGroupUserDto.getUsers();
        return usersGroupUserRepository.deleteUsersFromGroup(usersIds, groupId);
    }

    @Override
    public void addUserToGroupByCode(Long userId, Integer groupCode) {
        usersGroupUserRepository.addUserToGroup(userId, groupCode);
    }

    @Override
    public List<Long> findUsersByGroupId(Long groupId) {
        return usersGroupUserRepository.findUsersByGroupId(groupId);
    }

    @Override
    public List<UserGroupDto> getByIds(List<Long> groupIds) {
        return usersGroupsRepository.getByIds(groupIds)
                .stream()
                .map(this::userGroupDtoFromUserGroup)
                .collect(Collectors.toList());
    }
}