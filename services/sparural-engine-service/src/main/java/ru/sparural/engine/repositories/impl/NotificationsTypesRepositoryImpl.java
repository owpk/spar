package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.NotificationsType;
import ru.sparural.engine.repositories.NotificationsTypesRepository;
import ru.sparural.tables.NotificationsTypes;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationsTypesRepositoryImpl implements NotificationsTypesRepository {

    private final DSLContext dslContext;
    private final NotificationsTypes table = NotificationsTypes.NOTIFICATIONS_TYPES;

    @Override
    public List<NotificationsType> list(Integer offset, Integer limit) {
        return dslContext.selectFrom(table)
                .orderBy(table.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(NotificationsType.class);
    }

    @Override
    public Optional<NotificationsType> get(Long id) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(NotificationsType.class);
    }
}
