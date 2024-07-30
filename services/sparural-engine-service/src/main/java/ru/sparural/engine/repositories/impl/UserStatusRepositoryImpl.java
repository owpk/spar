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
                .set(table.USERID, userId)
                .set(table.CLIENTSTATUSID, id)
                .set(table.CURRENTVALUE, currentValue)
                .set(table.LEFTUNTILNEXTSTATUS, leftUntilNextStatus)
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .onConflict(table.USERID, table.CLIENTSTATUSID)
                .doNothing().executeAsync();
    }
}
