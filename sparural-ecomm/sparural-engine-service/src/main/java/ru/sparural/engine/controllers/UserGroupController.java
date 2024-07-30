package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.UserGroupDto;
import ru.sparural.engine.api.dto.UsersGroupUserDto;
import ru.sparural.engine.api.dto.common.LongList;
import ru.sparural.engine.services.UserGroupService;
import ru.sparural.engine.services.exception.UserNotFoundException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class UserGroupController {

    private final UserGroupService userGroupService;

    @KafkaSparuralMapping("users-groups/index")
    public List<UserGroupDto> list(
            @RequestParam Integer offset,
            @RequestParam Integer limit,
            @RequestParam String search
    ) {
        return userGroupService.list(offset, limit, search);
    }

    @KafkaSparuralMapping("users-groups/get")
    public UserGroupDto get(@RequestParam Long id) throws UserNotFoundException {
        return userGroupService.get(id);
    }

    @KafkaSparuralMapping("users-groups/listByIds")
    public List<UserGroupDto> getByIds(@Payload LongList groupIds) throws UserNotFoundException {
        return userGroupService.getByIds(groupIds.getList());
    }

    @KafkaSparuralMapping("users-groups/create")
    public UserGroupDto create(@Payload UserGroupDto user) throws UserNotFoundException {
        return userGroupService.create(user);
    }

    @KafkaSparuralMapping("users-groups/update")
    public UserGroupDto update(@Payload UserGroupDto userGroupDto, @RequestParam Long id) throws UserNotFoundException {
        return userGroupService.update(userGroupDto, id);
    }

    @KafkaSparuralMapping("users-groups/delete")
    public boolean delete(@RequestParam Long id) {
        return userGroupService.delete(id);
    }

    @KafkaSparuralMapping("users-groups/add-users")
    public boolean addUsers(@Payload UsersGroupUserDto usersGroupUserDto, @RequestParam Long id) {
        return userGroupService.addUsers(usersGroupUserDto, id);
    }

    @KafkaSparuralMapping("users-groups/delete-users")
    public boolean removeUsers(@Payload UsersGroupUserDto usersGroupUserDto, @RequestParam Long id) {
        return userGroupService.removeUsers(usersGroupUserDto, id);
    }

}
