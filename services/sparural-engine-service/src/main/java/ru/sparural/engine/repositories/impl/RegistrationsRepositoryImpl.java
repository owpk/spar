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
import ru.sparural.tables.UsersGroupUser;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */

@Service
@RequiredArgsConstructor
public class RegistrationsRepositoryImpl implements RegistrationsRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<Registrations> create(Registrations data) {
        return dslContext.insertInto(ru.sparural.tables.Registrations.REGISTRATIONS)
                .set(ru.sparural.tables.Registrations.REGISTRATIONS.USERID, data.getUserId())
                .set(ru.sparural.tables.Registrations.REGISTRATIONS.STEP, data.getStep())
                .set(ru.sparural.tables.Registrations.REGISTRATIONS.CREATEDAT, TimeHelper.currentTime())
                .returning().fetchOptionalInto(Registrations.class);
    }

    @Override
    public Optional<Registrations> update(Registrations data) {
        return dslContext.update(ru.sparural.tables.Registrations.REGISTRATIONS)
                .set(ru.sparural.tables.Registrations.REGISTRATIONS.USERID, data.getUserId())
                .set(ru.sparural.tables.Registrations.REGISTRATIONS.STEP, data.getStep())
                .set(ru.sparural.tables.Registrations.REGISTRATIONS.UPDATEDAT, TimeHelper.currentTime())
                .where(ru.sparural.tables.Registrations.REGISTRATIONS.ID.eq(data.getId()))
                .returning()
                .fetchOptionalInto(Registrations.class);
    }

    private SelectJoinStep<Record3<Long, Long, Integer>> basicSelect() {
        return dslContext.select(
                        ru.sparural.tables.Registrations.REGISTRATIONS.ID,
                        ru.sparural.tables.Registrations.REGISTRATIONS.USERID,
                        ru.sparural.tables.Registrations.REGISTRATIONS.STEP)
                .from(ru.sparural.tables.Registrations.REGISTRATIONS);
    }

    @Override
    public Optional<Registrations> getByUserId(Long userId) {
        return basicSelect()
                .where(ru.sparural.tables.Registrations.REGISTRATIONS.USERID.eq(userId))
                .fetchOptionalInto(Registrations.class);
    }

    @Override
    public Optional<Registrations> getByPhoneNumber(String phoneNumber) {
        return basicSelect()
                .where(ru.sparural.tables.Registrations.REGISTRATIONS.users().PHONENUMBER.eq(phoneNumber))
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
                .on(ru.sparural.tables.Registrations.REGISTRATIONS.USERID.eq(UsersGroupUser.USERS_GROUP_USER.USERID));
        Condition condition = null;
        if (definedUsers != null)
            condition = ru.sparural.tables.Registrations.REGISTRATIONS.USERID.in(definedUsers);
        if (groupId != null) {
            if (condition != null)
                condition = condition.and(UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.eq(groupId));
            else condition = UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.eq(groupId);
        }
        if (condition != null)
            return select.where(condition).fetchInto(Registrations.class);
        else return select.fetchInto(Registrations.class);
    }

}
