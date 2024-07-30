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
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONE_NUMBER, userUnconfirmedPhone.getPhoneNumber())
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID, userUnconfirmedPhone.getUserId())
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.CREATED_AT, TimeHelper.currentTime())
                .onConflict(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONE_NUMBER)
                .doUpdate()
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONE_NUMBER, userUnconfirmedPhone.getPhoneNumber())
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID, userUnconfirmedPhone.getUserId())
                .set(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.UPDATED_AT, TimeHelper.currentTime())
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
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONE_NUMBER,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID
                )
                .from(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .where(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID.eq(userId))
                .limit(1)
                .fetchOptionalInto(UserUnconfirmedPhone.class);
    }

    @Override
    public boolean deleteByUserId(Long id) {
        return dslContext.delete(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .where(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID.eq(id))
                .execute() > 0;
    }

    @Override
    public boolean removeByPhone(String phone) {
        return dslContext.delete(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .where(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONE_NUMBER.eq(phone))
                .execute() > 0;
    }

    @Override
    public Optional<UserUnconfirmedPhone> getLastByUserId(Long userId) {
        return dslContext.select(
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.ID,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.PHONE_NUMBER,
                        UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID
                )
                .from(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES)
                .where(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.USER_ID.eq(userId))
                .orderBy(UsersUnconfirmedPhones.USERS_UNCONFIRMED_PHONES.CREATED_AT.desc())
                .limit(1)
                .fetchOptionalInto(UserUnconfirmedPhone.class);
    }
}