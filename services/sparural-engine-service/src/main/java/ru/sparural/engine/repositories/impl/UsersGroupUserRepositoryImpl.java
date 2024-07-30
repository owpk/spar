package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.repositories.UsersGroupUserRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Users;
import ru.sparural.tables.UsersGroupUser;
import ru.sparural.tables.UsersGroups;
import ru.sparural.tables.daos.UsersGroupUserDao;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersGroupUserRepositoryImpl implements UsersGroupUserRepository {

    private final DSLContext dslContext;
    private UsersGroupUserDao dao;

    @PostConstruct
    public void init() {
        dao = new UsersGroupUserDao(dslContext.configuration());
    }

    @Transactional
    public boolean addUsersToGroup(List<Long> users, Long groupId) {
        var table = ru.sparural.tables.UsersGroupUser.USERS_GROUP_USER;
        users.forEach(id -> {
            var opt = dslContext.select().from(Users.USERS).where(Users.USERS.ID.eq(id)).fetchOptional();
            if (opt.isPresent())
                dslContext.insertInto(table)
                        .set(table.USERSGROUPID, groupId)
                        .set(table.USERID, id)
                        .set(table.CREATEDAT, TimeHelper.currentTime())
                        .onConflict(table.USERSGROUPID, table.USERID)
                        .doNothing()
                        .execute();
            else log.warn("User not present: " + id);
        });
        return true;
    }

    @Transactional
    public boolean deleteUsersFromGroup(List<Long> users, Long groupId) {
        users.forEach(id -> {
            var opt = dslContext.select().from(Users.USERS).where(Users.USERS.ID.eq(id)).fetchOptional();
            if (opt.isPresent())
                dslContext.delete(ru.sparural.tables.UsersGroupUser.USERS_GROUP_USER)
                        .where(ru.sparural.tables.UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.eq(groupId))
                        .and(ru.sparural.tables.UsersGroupUser.USERS_GROUP_USER.USERID.eq(id))
                        .execute();
            else log.warn("User not present: " + id);
        });
        return true;
    }

    @Override
    @Transactional
    public void addUserToGroup(Long userId, Integer groupCode) {
        dslContext.insertInto(ru.sparural.tables.UsersGroupUser.USERS_GROUP_USER)
                .set(ru.sparural.tables.UsersGroupUser.USERS_GROUP_USER.USERID, userId)
                .set(UsersGroupUser.USERS_GROUP_USER.USERSGROUPID, dslContext.select(UsersGroups.USERS_GROUPS.ID)
                        .from(UsersGroups.USERS_GROUPS)
                        .where(UsersGroups.USERS_GROUPS.CODE.eq(groupCode))
                        .fetchOneInto(ru.sparural.tables.pojos.UsersGroups.class)
                        .getId())
                .set(UsersGroupUser.USERS_GROUP_USER.CREATEDAT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public List<Long> findUsersByGroupId(Long groupId) {
        return dslContext.select(UsersGroupUser.USERS_GROUP_USER.USERID)
                .from(ru.sparural.tables.UsersGroupUser.USERS_GROUP_USER)
                .where(UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.eq(groupId))
                .fetchInto(Long.class);
    }

}