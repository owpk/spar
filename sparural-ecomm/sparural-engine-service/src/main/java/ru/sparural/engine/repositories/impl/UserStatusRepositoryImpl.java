package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.repositories.UserStatusRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.ClientStatusUser;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class UserStatusRepositoryImpl implements UserStatusRepository {

    private final DSLContext dslContext;
    private final ClientStatusUser table = ClientStatusUser.CLIENT_STATUS_USER;

    @Override
    public void bind(Long id, Long userId, Integer currentValue, Integer leftUntilNextStatus) {
        dslContext.insertInto(table)
                .set(table.USER_ID, userId)
                .set(table.CLIENT_STATUS_ID, id)
                .set(table.CURRENT_VALUE, currentValue)
                .set(table.LEFT_UNTIL_NEXT_STATUS, leftUntilNextStatus)
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .onConflict(table.USER_ID, table.CLIENT_STATUS_ID)
                .doNothing().executeAsync();
    }
}
