package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.ClientStatusEntity;
import ru.sparural.engine.repositories.ClientStatusRepository;
import ru.sparural.tables.ClientStatuses;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientStatusRepositoryImpl implements ClientStatusRepository {

    private final DSLContext dslContext;

    @Override
    public List<ClientStatusEntity> fetch(int offset, int limit) {
        return dslContext.selectFrom(ClientStatuses.CLIENT_STATUSES)
                .orderBy(ClientStatuses.CLIENT_STATUSES.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(ClientStatusEntity.class);
    }

    @Override
    public Optional<ClientStatusEntity> get(Long id) {
        return dslContext
                .selectFrom(ClientStatuses.CLIENT_STATUSES)
                .where(ClientStatuses.CLIENT_STATUSES.ID.eq(id))
                .fetchOptionalInto(ClientStatusEntity.class);
    }
}
