package ru.sparural.backgrounds.services;

import org.springframework.cache.annotation.Cacheable;
import ru.sparural.backgrounds.cache.CacheNames;
import ru.sparural.engine.api.dto.user.LoymaxUserDto;
import ru.sparural.engine.api.dto.user.RoleDto;
import ru.sparural.engine.api.dto.user.UserDto;

import java.util.List;

public interface EngineUserService {
    List<UserDto> loadAllUsers(Long roleId);

    List<LoymaxUserDto> loadAllLoymaxUsers();

    List<RoleDto> listRoles();

}
