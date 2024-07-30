package ru.sparural.engine.repositories.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.UserGroup;
import ru.sparural.engine.repositories.UsersGroupsRepository;
import ru.sparural.tables.UsersGroups;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersGroupsRepositoryImpl implements UsersGroupsRepository {

    private final DSLContext dslContext;

    @Override
    public List<UserGroup> list(int offset, int limit, String name) {
        return dslContext
                .select(
                        UsersGroups.USERS_GROUPS.ID,
                        UsersGroups.USERS_GROUPS.NAME,
                        UsersGroups.USERS_GROUPS.ISSYS
                )
                .from(UsersGroups.USERS_GROUPS)
                .where(UsersGroups.USERS_GROUPS.NAME.like("%" + name + "%"))
                .and(UsersGroups.USERS_GROUPS.CREATEDAT.notEqual(UsersGroups.USERS_GROUPS.UPDATEDAT))
                .orderBy(UsersGroups.USERS_GROUPS.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .into(UserGroup.class);
    }

    @Override
    public Optional<UserGroup> get(Long id) {
        return dslContext.select(
                        UsersGroups.USERS_GROUPS.ID,
                        UsersGroups.USERS_GROUPS.NAME,
                        UsersGroups.USERS_GROUPS.ISSYS
                )
                .from(UsersGroups.USERS_GROUPS)
                .where(UsersGroups.USERS_GROUPS.ID.eq(id))
                //.and(UsersGroups.USERS_GROUPS.ISSYS.eq(true))
                .fetchOptionalInto(UserGroup.class);
    }

    @Override
    public UserGroup create(UserGroup data) {
        var update = dslContext.insertInto(UsersGroups.USERS_GROUPS)
                .set(UsersGroups.USERS_GROUPS.NAME, data.getName())
                .set(UsersGroups.USERS_GROUPS.ISSYS, false)
                .set(UsersGroups.USERS_GROUPS.CREATEDAT, new Date().getTime())
                .set(UsersGroups.USERS_GROUPS.UPDATEDAT, new Date().getTime())
                .returning().fetchOne();
        if (update == null)
            return null;
        return update.into(UserGroup.class);
    }

    @Override
    public UserGroup update(UserGroup data, Long id) {
        if (data.getIsSys() == null) {
            data.setIsSys(false);
        }
        var update = dslContext.update(UsersGroups.USERS_GROUPS)
                .set(UsersGroups.USERS_GROUPS.NAME, data.getName())
                .set(UsersGroups.USERS_GROUPS.ISSYS, data.getIsSys())
                .set(UsersGroups.USERS_GROUPS.UPDATEDAT, new Date().getTime())
                .where(UsersGroups.USERS_GROUPS.ID.eq(id))
                .returning().fetchOne();
        if (update == null)
            return null;
        return update.into(UserGroup.class);
    }

    @Override
    public boolean delete(Long id) {
        return dslContext.delete(UsersGroups.USERS_GROUPS)
                .where(UsersGroups.USERS_GROUPS.ID.eq(id))
                .and(UsersGroups.USERS_GROUPS.ISSYS.eq(false))
                .execute() == 1;
    }

    @Override
    public UserGroup findById(Long id) {
        var group = dslContext.select(
                        UsersGroups.USERS_GROUPS.ID,
                        UsersGroups.USERS_GROUPS.NAME,
                        UsersGroups.USERS_GROUPS.ISSYS
                )
                .from(UsersGroups.USERS_GROUPS)
                .where(UsersGroups.USERS_GROUPS.ID.eq(id))
                .fetchOne();
        if (group == null)
            return null;

        return group.into(UserGroup.class);
    }

    @Override
    public List<UserGroup> getByIds(List<Long> groupIds) {
        return Lists.partition(groupIds, 1000)
                .parallelStream()
                .map(part ->
                        dslContext.select()
                                .from(UsersGroups.USERS_GROUPS)
                                .where(UsersGroups.USERS_GROUPS.ID.in(part))
                                .fetchInto(UserGroup.class)
                )
                .flatMap(part -> part.stream())
                .collect(Collectors.toList());
    }
}
