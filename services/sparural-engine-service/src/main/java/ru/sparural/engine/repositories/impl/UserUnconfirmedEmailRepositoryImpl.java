package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.UserUnconfirmedEmail;
import ru.sparural.engine.repositories.UserUnconfirmedEmailRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.UsersUnconfirmedEmails;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class UserUnconfirmedEmailRepositoryImpl implements UserUnconfirmedEmailRepository {

    private final DSLContext dslContext;

    @Override
    public void createRecord(UserUnconfirmedEmail userUnconfirmedEmail) {
        dslContext.insertInto(ru.sparural.tables.UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS)
                .set(ru.sparural.tables.UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS.EMAIL, userUnconfirmedEmail.getEmail())
                .set(ru.sparural.tables.UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS.USERID, userUnconfirmedEmail.getUserId())
                .set(ru.sparural.tables.UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS.CREATEDAT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public Optional<UserUnconfirmedEmail> getByUserId(Long userId) {
        return dslContext.select()
                .from(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS)
                .where(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS.USERID.eq(userId))
                .limit(1)
                .fetchOptionalInto(UserUnconfirmedEmail.class);
    }

    @Override
    public void removeByEmail(String email) {
        dslContext.deleteFrom(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS)
                .where(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS.EMAIL.eq(email))
                .execute();
    }

    @Override
    public void removeByUser(Long userId) {
        dslContext.deleteFrom(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS)
                .where(UsersUnconfirmedEmails.USERS_UNCONFIRMED_EMAILS.USERID.eq(userId))
                .execute();
    }
}
