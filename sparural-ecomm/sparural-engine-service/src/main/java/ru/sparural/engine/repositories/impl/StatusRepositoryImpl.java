package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.ClientStatusEntity;
import ru.sparural.engine.repositories.StatusRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.ClientStatuses;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class StatusRepositoryImpl implements StatusRepository {

    private final DSLContext dslContext;
    private final ClientStatuses table = ClientStatuses.CLIENT_STATUSES;

    @Override
    @Transactional
    public Optional<ClientStatusEntity> saveOrUpdate(ClientStatusEntity clientStatus) {
        return dslContext.insertInto(table)
                .set(table.NAME, clientStatus.getName())
                .set(table.THRESHOLD, clientStatus.getThreshold())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.NAME)
                .doUpdate()
                .set(table.THRESHOLD, clientStatus.getThreshold())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(ClientStatusEntity.class);
    }
}
