package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import ru.sparural.engine.entity.Role;
import ru.sparural.engine.repositories.impl.RolesRepositoryImpl;
import ru.sparural.engine.services.RoleService;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RolesRepositoryImpl rolesRepository;

    public Role getByName(String name) {
        return rolesRepository.getByName(name)
                .orElseThrow(() -> new ResourceAccessException("role not found: " + name));
    }

}
