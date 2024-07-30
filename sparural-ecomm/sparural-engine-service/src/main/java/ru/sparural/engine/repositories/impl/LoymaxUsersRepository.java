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

import static org.jooq.impl.DSL.coalesce;
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
        return findWhere(LoymaxUsers.LOYMAX_USERS.PERSON_UID.eq(personUid));
    }

    public Optional<LoymaxUser> getByUserId(Long userId) {
        return findWhere(LoymaxUsers.LOYMAX_USERS.USER_ID.eq(userId));
    }

    private Optional<LoymaxUser> findWhere(Condition condition) {
        return dsl.select(
                        table.ID,
                        table.USER_ID,
                        table.EXPIRES_AT,
                        table.TOKEN,
                        table.REFRESH_TOKEN,
                        table.PERSON_UID,
                        table.LOYMAX_USER_ID,
                        table.SET_MOBILE_APPLICATION_INSTALLED)
                .from(table)
                .where(condition)
                .fetchOptionalInto(LoymaxUser.class);
    }

    public Optional<LoymaxUser> update(LoymaxUser loymaxUser) {
        return dsl.update(LoymaxUsers.LOYMAX_USERS)
                .set(table.REFRESH_TOKEN, loymaxUser.getRefreshToken())
                .set(table.EXPIRES_AT, loymaxUser.getExpiresAt())
                .set(table.TOKEN, loymaxUser.getToken())
                .set(table.PERSON_UID, loymaxUser.getPersonUid())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .set(table.SET_MOBILE_APPLICATION_INSTALLED, coalesce(val(loymaxUser.getSetMobileApplicationInstalled()),
                        table.SET_MOBILE_APPLICATION_INSTALLED))
                .where(table.ID.eq(loymaxUser.getId()))
                .returningResult()
                .fetchOptionalInto(LoymaxUser.class);
    }

    public Optional<LoymaxUser> updateForLocalUser(LoymaxUser loymaxUser) {
        return dsl.update(LoymaxUsers.LOYMAX_USERS)
                .set(table.REFRESH_TOKEN, loymaxUser.getRefreshToken())
                .set(table.EXPIRES_AT, loymaxUser.getExpiresAt())
                .set(table.TOKEN, loymaxUser.getToken())
                .set(table.LOYMAX_USER_ID, loymaxUser.getId())
                .set(table.PERSON_UID, loymaxUser.getPersonUid())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .set(table.SET_MOBILE_APPLICATION_INSTALLED, coalesce(val(loymaxUser.getSetMobileApplicationInstalled()),
                        table.SET_MOBILE_APPLICATION_INSTALLED))
                .where(table.USER_ID.eq(loymaxUser.getUserId()))
                .returningResult()
                .fetchOptionalInto(LoymaxUser.class);
    }

    public Optional<LoymaxUser> saveOrUpdate(LoymaxUser loymaxUser) {
        if (checkIfLoymaxUserBindToLocalUser(loymaxUser.getUserId()))
            return updateForLocalUser(loymaxUser);
        return dsl
                .insertInto(LoymaxUsers.LOYMAX_USERS)
                .set(table.USER_ID, loymaxUser.getUserId())
                .set(table.REFRESH_TOKEN, loymaxUser.getRefreshToken())
                .set(table.EXPIRES_AT, loymaxUser.getExpiresAt())
                .set(table.TOKEN, loymaxUser.getToken())
                .set(table.LOYMAX_USER_ID, loymaxUser.getId())
                .set(table.PERSON_UID, loymaxUser.getPersonUid())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .set(table.SET_MOBILE_APPLICATION_INSTALLED, loymaxUser.getSetMobileApplicationInstalled())
                .onConflict(table.PERSON_UID)
                .doUpdate()
                .set(table.USER_ID, DSL.coalesce(table.as("excluded").USER_ID, loymaxUser.getUserId()))
                .set(table.REFRESH_TOKEN, loymaxUser.getRefreshToken())
                .set(table.EXPIRES_AT, loymaxUser.getExpiresAt())
                .set(table.TOKEN, loymaxUser.getToken())
                .set(table.LOYMAX_USER_ID, loymaxUser.getId())
                .set(table.SET_MOBILE_APPLICATION_INSTALLED, loymaxUser.getSetMobileApplicationInstalled())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returningResult()
                .fetchOptionalInto(LoymaxUser.class);
    }

    public boolean checkIfLoymaxUserBindToLocalUser(Long userId) {
        return dsl.select(table.ID).from(table)
                .where(table.USER_ID.eq(userId))
                .fetchOptional().isPresent();
    }

    public List<LoymaxUser> fetchAllUsers() {
        return dsl.selectFrom(table)
                .where(table.LOYMAX_USER_ID.isNotNull())
                .fetchInto(LoymaxUser.class);
    }
}