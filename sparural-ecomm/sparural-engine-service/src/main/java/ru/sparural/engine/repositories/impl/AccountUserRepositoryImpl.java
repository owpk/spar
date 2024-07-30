package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.AccountUserEntity;
import ru.sparural.engine.repositories.AccountUserRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.AccountUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountUserRepositoryImpl implements AccountUserRepository {

    private final DSLContext dslContext;

    @Override
    public void save(Long userId, Long accountId) {
        dslContext
                .insertInto(AccountUser.ACCOUNT_USER)
                .set(AccountUser.ACCOUNT_USER.USER_ID, userId)
                .set(AccountUser.ACCOUNT_USER.ACCOUNT_ID, accountId)
                .set(AccountUser.ACCOUNT_USER.CREATED_AT, TimeHelper.currentTime())
                .onConflict(AccountUser.ACCOUNT_USER.USER_ID, AccountUser.ACCOUNT_USER.ACCOUNT_ID)
                .doNothing()
                .returning()
                .fetchOptionalInto(AccountUserEntity.class);
    }

    @Override
    public void batchBind(Long userId, List<Long> ids) {
        var table = AccountUser.ACCOUNT_USER;
        var insert = dslContext
                .insertInto(table, table.ACCOUNT_ID, table.USER_ID, table.CREATED_AT);
        for (var id : ids) {
            insert = insert
                    .values(id, userId, TimeHelper.currentTime());
            insert
                    .onConflict(table.ACCOUNT_ID, table.USER_ID)
                    .doNothing();
        }
        insert.executeAsync();
    }
}
