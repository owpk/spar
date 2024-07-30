package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Role;
import ru.sparural.engine.repositories.RolesRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.RoleUser;
import ru.sparural.tables.Roles;
import ru.sparural.tables.daos.RoleUserDao;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolesRepositoryImpl implements RolesRepository {

    private final DSLContext dslContext;
    private RoleUserDao roleUserDao;

    @PostConstruct
    public void init() {
        roleUserDao = new RoleUserDao(dslContext.configuration());
    }

    @Override
    public List<Role> getListByUserIDs(Long userID) {
        return dslContext
                .select()
                .from(Roles.ROLES)
                .innerJoin(RoleUser.ROLE_USER)
                .on(RoleUser.ROLE_USER.USER_ID.eq(userID))
                .fetch()
                .intoGroups(Roles.ROLES.fields())
                .values()
                .stream()
                .map(r -> r.into().get(0).into(Role.class))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<Role>> getListByUserIDs(List<Long> userID) {
        Map<Long, List<Role>> response = new HashMap<>();
        var res = dslContext
                .select()
                .from(Roles.ROLES)
                .leftJoin(RoleUser.ROLE_USER)
                .on(Roles.ROLES.ID.eq(RoleUser.ROLE_USER.ROLE_ID))
                .where(RoleUser.ROLE_USER.USER_ID.in(userID))
                .fetch();
        res.forEach(r -> {
            var row = r.into(Role.class);
            var userId = r.get(RoleUser.ROLE_USER.USER_ID);
            response.computeIfAbsent(userId, k -> new ArrayList<>());
            response.get(userId).add(row);
        });
        return response;
    }

    public List<Role> getListByNames(List<String> names) {
        return dslContext
                .selectFrom(Roles.ROLES)
                .where(Roles.ROLES.CODE.in(names))
                .fetch()
                .into(Role.class);
    }

    public Optional<Role> getByName(String name) {
        return dslContext
                .selectFrom(Roles.ROLES)
                .where(Roles.ROLES.CODE.eq(name))
                .fetchOptionalInto(Role.class);
    }

    @Override
    public List<Role> getAll() {
        return dslContext.select()
                .from(Roles.ROLES)
                .fetch()
                .map(x -> x.into(Role.class));
    }

    @Override
    public void deleteRoleForUser(Role role, Long userId) {
        var roleUser = roleUserDao.fetchByUserId(userId).stream().filter(
                x -> role.getId().equals(x.getRoleId())).findFirst().orElseThrow();
        roleUserDao.deleteById(roleUser.getId());
    }

    @Override
    public void addRoleForUser(Role role, Long userId) {
        var roleUserRecord = new ru.sparural.tables.pojos.RoleUser();
        roleUserRecord.setRoleId(role.getId());
        roleUserRecord.setUserId(userId);
        roleUserRecord.setUpdatedAt(TimeHelper.currentTime());
        roleUserRecord.setCreatedAt(TimeHelper.currentTime());
        roleUserDao.insert(roleUserRecord);
    }
}
