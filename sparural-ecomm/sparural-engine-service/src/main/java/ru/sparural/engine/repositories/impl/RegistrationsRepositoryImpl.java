package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Registrations;
import ru.sparural.engine.repositories.RegistrationsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Users;
import ru.sparural.tables.UsersGroupUser;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */

@Service
@RequiredArgsConstructor
public class RegistrationsRepositoryImpl implements RegistrationsRepository {
    private final ru.sparural.tables.Registrations regTable = ru.sparural.tables.Registrations.REGISTRATIONS;
    private final DSLContext dslContext;

    @Override
    public Optional<Registrations> create(Registrations data) {
        return dslContext.insertInto(regTable)
                .set(regTable.USER_ID, data.getUserId())
                .set(regTable.STEP, data.getStep())
                .set(regTable.CREATED_AT, TimeHelper.currentTime())
                .returning().fetchOptionalInto(Registrations.class);
    }

    @Override
    public Optional<Registrations> update(Registrations data) {
        return dslContext.update(regTable)
                .set(regTable.USER_ID, data.getUserId())
                .set(regTable.STEP, data.getStep())
                .set(regTable.UPDATED_AT, TimeHelper.currentTime())
                .where(regTable.ID.eq(data.getId()))
                .returning()
                .fetchOptionalInto(Registrations.class);
    }

    private SelectJoinStep<Record3<Long, Long, Integer>> basicSelect() {
        return dslContext.select(
                        regTable.ID,
                        regTable.USER_ID,
                        regTable.STEP)
                .from(regTable);
    }

    @Override
    public Optional<Registrations> getByUserId(Long userId) {
        return basicSelect()
                .where(regTable.USER_ID.eq(userId))
                .fetchOptionalInto(Registrations.class);
    }

    @Override
    public Optional<Registrations> getByPhoneNumber(String phoneNumber) {
        return basicSelect()
                .leftJoin(Users.USERS).on(regTable.USER_ID.eq(Users.USERS.ID))
                .where(Users.USERS.PHONE_NUMBER.eq(phoneNumber))
                .fetchOptionalInto(Registrations.class);
    }

    @Override
    public List<Registrations> getAll() {
        return basicSelect().fetchInto(Registrations.class);
    }

    @Override
    public List<Registrations> findUsersWithNotCompletedRegistrations(List<Long> definedUsers, Long groupId) {
        var select = basicSelect()
                .leftJoin(UsersGroupUser.USERS_GROUP_USER)
                .on(regTable.USER_ID.eq(UsersGroupUser.USERS_GROUP_USER.USER_ID));
        Condition condition = null;
        if (definedUsers != null)
            condition = regTable.USER_ID.in(definedUsers);
        if (groupId != null) {
            if (condition != null)
                condition = condition.and(UsersGroupUser.USERS_GROUP_USER.USERS_GROUP_ID.eq(groupId));
            else condition = UsersGroupUser.USERS_GROUP_USER.USERS_GROUP_ID.eq(groupId);
        }
        if (condition != null)
            return select.where(condition).fetchInto(Registrations.class);
        else return select.fetchInto(Registrations.class);
    }

}
