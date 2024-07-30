package ru.sparural.engine.services;

import ru.sparural.engine.entity.Role;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RoleService {
    Role getByName(String name);
}
