package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.LoymaxUsers;
import ru.sparural.tables.records.LoymaxUsersRecord;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.val;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class LoymaxUsersRepository extends CrudRepositoryImpl<Long, LoymaxUsersRecord, LoymaxUsers> {
    @PostConstruct
    public void init() {
        this.table = LoymaxUsers.LOYMAX_USERS;
        this.idFieldName = LoymaxUsers.LOYMAX_USERS.ID.getName();
    }

    public Optional<LoymaxUser> getByPersonUid(String personUid) {
        return findWhere(LoymaxUsers.LOYMAX_USERS.PERSONUID.eq(personUid));
    }

    public Optional<LoymaxUser> getByUserId(Long userId) {
        return findWhere(LoymaxUsers.LOYMAX_USERS.USERID.eq(userId));
    }

    private Optional<LoymaxUser> findWhere(Condition condition) {
        return dsl.select(
                        table.ID,
                        table.USERID,
                        table.EXPIRESAT,
                        table.TOKEN,
                        table.REFRESHTOKEN,
                        table.PERSONUID,
                        table.LOYMAXUSERID,
                        table.SETMOBILEAPPLICATIONINSTALLED)
                .from(table)
                .where(condition)
                .fetchOptionalInto(LoymaxUser.class);
    }

    public Optional<LoymaxUser> update(LoymaxUser loymaxUser) {
        return dsl.update(LoymaxUsers.LOYMAX_USERS)
                .set(table.REFRESHTOKEN, loymaxUser.getRefreshToken())
                .set(table.EXPIRESAT, loymaxUser.getExpiresAt())
                .set(table.TOKEN, loymaxUser.getToken())
                .set(table.PERSONUID, loymaxUser.getPersonUid())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .set(table.SETMOBILEAPPLICATIONINSTALLED, coalesce(val(loymaxUser.getSetMobileApplicationInstalled()),
                        table.SETMOBILEAPPLICATIONINSTALLED))
                .where(table.ID.eq(loymaxUser.getId()))
                .returningResult()
                .fetchOptionalInto(LoymaxUser.class);
    }

    public Optional<LoymaxUser> updateForLocalUser(LoymaxUser loymaxUser) {
        return dsl.update(LoymaxUsers.LOYMAX_USERS)
                .set(table.REFRESHTOKEN, loymaxUser.getRefreshToken())
                .set(table.EXPIRESAT, loymaxUser.getExpiresAt())
                .set(table.TOKEN, loymaxUser.getToken())
                .set(table.LOYMAXUSERID, loymaxUser.getId())
                .set(table.PERSONUID, loymaxUser.getPersonUid())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .set(table.SETMOBILEAPPLICATIONINSTALLED, coalesce(val(loymaxUser.getSetMobileApplicationInstalled()),
                        table.SETMOBILEAPPLICATIONINSTALLED))
                .where(table.USERID.eq(loymaxUser.getUserId()))
                .returningResult()
                .fetchOptionalInto(LoymaxUser.class);
    }

    public Optional<LoymaxUser> saveOrUpdate(LoymaxUser loymaxUser) {
        if (checkIfLoymaxUserBindToLocalUser(loymaxUser.getUserId()))
            return updateForLocalUser(loymaxUser);
        return dsl
                .insertInto(LoymaxUsers.LOYMAX_USERS)
                .set(table.USERID, loymaxUser.getUserId())
                .set(table.REFRESHTOKEN, loymaxUser.getRefreshToken())
                .set(table.EXPIRESAT, loymaxUser.getExpiresAt())
                .set(table.TOKEN, loymaxUser.getToken())
                .set(table.LOYMAXUSERID, loymaxUser.getId())
                .set(table.PERSONUID, loymaxUser.getPersonUid())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .set(table.SETMOBILEAPPLICATIONINSTALLED, loymaxUser.getSetMobileApplicationInstalled())
                .onConflict(table.PERSONUID)
                .doUpdate()
                .set(table.USERID, DSL.coalesce(table.as("excluded").USERID, loymaxUser.getUserId()))
                .set(table.REFRESHTOKEN, loymaxUser.getRefreshToken())
                .set(table.EXPIRESAT, loymaxUser.getExpiresAt())
                .set(table.TOKEN, loymaxUser.getToken())
                .set(table.LOYMAXUSERID, loymaxUser.getId())
                .set(table.SETMOBILEAPPLICATIONINSTALLED, loymaxUser.getSetMobileApplicationInstalled())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returningResult()
                .fetchOptionalInto(LoymaxUser.class);
    }

    public boolean checkIfLoymaxUserBindToLocalUser(Long userId) {
        return dsl.select(table.ID).from(table)
                .where(table.USERID.eq(userId))
                .fetchOptional().isPresent();
    }

    public List<LoymaxUser> fetchAllUsers() {
        return dsl.selectFrom(table).fetchInto(LoymaxUser.class);
    }
}