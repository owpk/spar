package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.UserAttributesEntity;
import ru.sparural.engine.repositories.UserAttributesRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.UsersAttributeUser;
import ru.sparural.tables.UsersAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAttributesRepositoryImpl implements UserAttributesRepository {
    private final DSLContext dslContext;
    private final UsersAttributes table = UsersAttributes.USERS_ATTRIBUTES;
    private final UsersAttributeUser usersAttrTable = UsersAttributeUser.USERS_ATTRIBUTE_USER;

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

    @Override
    public void batchBind(Map<Long, Set<Long>> attributesToBind) {
        var queries = attributesToBind.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(attributeId ->
                        dslContext.insertInto(usersAttrTable)
                                .set(usersAttrTable.USER_ID, e.getKey())
                                .set(usersAttrTable.USER_ATTRIBUTE_ID, attributeId)
                                .set(usersAttrTable.CREATED_AT, TimeHelper.currentTime())
                                .onConflict(usersAttrTable.USER_ID, usersAttrTable.USER_ATTRIBUTE_ID)
                                .doNothing()
                )).collect(Collectors.toList());
        dslContext.batch(queries).execute();
    }

    @Override
    public void deleteAllByUserIds(Set<Long> userIds) {
        dslContext.delete(usersAttrTable).where(usersAttrTable.USER_ID.in(userIds)).execute();
    }

    @Override
    public Map<Long, Set<Long>> fetchAllByUserIds(Set<Long> userIds) {
        return dslContext.select().from(usersAttrTable)
                .where(usersAttrTable.USER_ID.in(userIds))
                .fetchGroups(usersAttrTable.USER_ID)
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().intoSet(usersAttrTable.USER_ATTRIBUTE_ID)));
    }

    @Override
    public List<UserAttributesEntity> batchSaveUserAttributes(List<UserAttributesEntity> entitiesToSave) {
        var records = entitiesToSave
                .stream()
                .map(entity -> dslContext.insertInto(table)
                        .set(table.ATTRIBUTE_NAME, entity.getAttributeName())
                        .set(table.NAME, entity.getName())
                        .set(table.CREATED_AT, TimeHelper.currentTime())
                        .onConflict(table.ATTRIBUTE_NAME)
                        .doUpdate()
                        .set(table.NAME, entity.getName())
                        .set(table.UPDATED_AT, TimeHelper.currentTime()))
                .collect(Collectors.toList());
        dslContext.batch(records).execute();
        return dslContext.select().from(table)
                .where(table.ATTRIBUTE_NAME.in(entitiesToSave.stream().map(UserAttributesEntity::getAttributeName).collect(Collectors.toList())))
                .fetchInto(UserAttributesEntity.class);
    }

    @Override
    public void deleteAllByUserIdAttributeId(Map<Long, Set<Long>> recordsToDelete) {
        var queries = recordsToDelete.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(attributeId ->
                        dslContext.delete(usersAttrTable)
                                .where(usersAttrTable.USER_ID.eq(e.getKey())
                                        .and(usersAttrTable.USER_ATTRIBUTE_ID.eq(attributeId)))))
                .collect(Collectors.toList());
        dslContext.batch(queries).execute();
    }
}
