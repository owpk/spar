package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.UserUnconfirmedPhone;
import ru.sparural.engine.repositories.UserUnconfirmedPhoneRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.UsersUnconfirmedPhones;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class UserUnconfirmedPhonesRepository implements UserUnconfirmedPhoneRepository {
    private final DSLContext dslContext;

    @Transactional
    public Optional<UserUnconfirmedPhone> saveOrUpdate(UserUnconfirmedPhone userUnconfirmedPhone) {
        var insertStep = dslContext
                .insertInto(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONENUMBER, userUnconfirmedPhone.getPhoneNumber())
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID, userUnconfirmedPhone.getUserId())
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.CREATEDAT, TimeHelper.currentTime())
                .onConflict(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONENUMBER)
                .doUpdate()
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONENUMBER, userUnconfirmedPhone.getPhoneNumber())
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID, userUnconfirmedPhone.getUserId())
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.UPDATEDAT, TimeHelper.currentTime())
                .returningResult(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.ID);
        var record = insertStep.fetchOne();
        if (record != null) {
            Long id = record.getValue(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.ID);
            userUnconfirmedPhone.setId(id);
            return Optional.of(userUnconfirmedPhone);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserUnconfirmedPhone> getByUserId(Long userId) {
        return dslContext.select(
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.ID,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONENUMBER,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID
                )
                .from(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .where(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID.eq(userId))
                .limit(1)
                .fetchOptionalInto(UserUnconfirmedPhone.class);
    }

    @Override
    public boolean deleteByUserId(Long id) {
        return dslContext.delete(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .where(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID.eq(id))
                .execute() > 0;
    }

    @Override
    public boolean removeByPhone(String phone) {
        return dslContext.delete(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .where(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONENUMBER.eq(phone))
                .execute() > 0;
    }

    @Override
    public Optional<UserUnconfirmedPhone> getLastByUserId(Long userId) {
        return dslContext.select(
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.ID,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONENUMBER,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID
                )
                .from(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .where(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USERID.eq(userId))
                .orderBy(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.CREATEDAT.desc())
                .limit(1)
                .fetchOptionalInto(UserUnconfirmedPhone.class);
    }
}