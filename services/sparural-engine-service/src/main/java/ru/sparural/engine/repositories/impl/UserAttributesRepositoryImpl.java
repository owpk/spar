package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.UserAttributesEntity;
import ru.sparural.engine.repositories.UserAttributesRepository;
import ru.sparural.tables.UsersAttributes;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAttributesRepositoryImpl implements UserAttributesRepository {
    private final DSLContext dslContext;
    private final UsersAttributes table = UsersAttributes.USERS_ATTRIBUTES;

    @Override
    public Optional<UserAttributesEntity> fetchById(Long id) {
        return dslContext.select().from(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(UserAttributesEntity.class);
    }

    @Override
    public List<UserAttributesEntity> list(Integer offset, Integer limit) {
        return dslContext.select().from(table)
                .offset(offset)
                .limit(limit)
                .fetchInto(UserAttributesEntity.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table).where(table.ID.eq(id)).execute() > 0;
    }

    @Override
    public Optional<UserAttributesEntity> update(Long id, UserAttributesEntity entity) {
        return dslContext.update(table)
                .set(table.ATTRIBUTE_NAME, entity.getAttributeName())
                .set(table.NAME, entity.getName())
                .where(table.ID.eq(id)).returning().fetchOptionalInto(UserAttributesEntity.class);
    }

    @Override
    public Optional<UserAttributesEntity> create(UserAttributesEntity data) {
        return dslContext.insertInto(table)
                .set(table.NAME, data.getName())
                .set(table.ATTRIBUTE_NAME, data.getAttributeName())
                .returning().fetchOptionalInto(UserAttributesEntity.class);
    }
}
