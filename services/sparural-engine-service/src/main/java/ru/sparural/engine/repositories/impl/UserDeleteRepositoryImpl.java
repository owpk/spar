package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Deregistration;
import ru.sparural.engine.repositories.UserDeleteRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.enums.UsersDeleteReasons;
import ru.sparural.tables.UsersDelete;

@Service
@RequiredArgsConstructor
public class UserDeleteRepositoryImpl implements UserDeleteRepository {

    private final DSLContext dslContext;
    private final UsersDelete table = UsersDelete.USERS_DELETE;

    @Override
    public void create(Deregistration deregistration) {
        dslContext
                .insertInto(table)
                .set(table.MESSAGE, deregistration.getMessage())
                .set(table.USERID, deregistration.getUserId())
                .set(table.REASON, deregistration.getReason() != null ?
                        UsersDeleteReasons.valueOf(deregistration.getReason()) : UsersDeleteReasons.Reject)
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .execute();
    }
}
