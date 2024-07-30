package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.DeviceType;
import ru.sparural.engine.entity.MinVersionAppDeviceTypeEntity;
import ru.sparural.engine.entity.MinVersionAppDeviceTypeFullEntity;
import ru.sparural.engine.repositories.MinVersionAppDeviceTypeRepository;
import ru.sparural.tables.DeviceTypes;
import ru.sparural.tables.MinVersionAppDeviceType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MinVersionAppDeviceTypeRepositoryImpl implements MinVersionAppDeviceTypeRepository {
    private final DSLContext dslContext;
    private final MinVersionAppDeviceType table = MinVersionAppDeviceType.MIN_VERSION_APP_DEVICE_TYPE;

    @Override
    public Optional<MinVersionAppDeviceTypeFullEntity> getByDeviceTypeName(String deviceType) {
        return dslContext.select().from(table)
                .leftJoin(DeviceTypes.DEVICE_TYPES)
                .on(table.DEVICE_TYPE_ID.eq(DeviceTypes.DEVICE_TYPES.ID))
                .where(DeviceTypes.DEVICE_TYPES.NAME.eq(deviceType))
                .fetchOptional(this::basicMap);
    }

    private MinVersionAppDeviceTypeFullEntity basicMap(Record rec) {
        var devTypeEntity = rec.into(DeviceTypes.DEVICE_TYPES.fields()).into(DeviceType.class);
        var entity = new MinVersionAppDeviceTypeFullEntity();
        entity.setId(rec.get(table.ID));
        entity.setDeviceType(devTypeEntity);
        entity.setMinVersionApp(rec.get(table.MIN_VERSION_APP));
        return entity;
    }

    @Override
    public Optional<MinVersionAppDeviceTypeEntity> create(MinVersionAppDeviceTypeEntity entity) {
        return dslContext.insertInto(table)
                .set(table.MIN_VERSION_APP, entity.getMinVersionApp())
                .set(table.DEVICE_TYPE_ID, entity.getDeviceTypeId())
                .set(table.CREATED_AT, new Date().getTime())
                .set(table.UPDATED_AT, new Date().getTime())
                .onConflict().doUpdate()
                .set(table.MIN_VERSION_APP, entity.getMinVersionApp())
                .set(table.UPDATED_AT, new Date().getTime())
                .returning()
                .fetchOptionalInto(MinVersionAppDeviceTypeEntity.class);
    }

    @Override
    public Optional<MinVersionAppDeviceTypeEntity> update(MinVersionAppDeviceTypeEntity entity, Long id) {
        return dslContext.update(table)
                .set(table.MIN_VERSION_APP, entity.getMinVersionApp())
                .where(table.ID.eq(id))
                .returning().fetchOptionalInto(MinVersionAppDeviceTypeEntity.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table)
                .where(table.ID.eq(id))
                .execute() > 0;
    }

    @Override
    public List<MinVersionAppDeviceTypeFullEntity> getAll() {
        return dslContext.select()
                .from(table)
                .leftJoin(DeviceTypes.DEVICE_TYPES)
                .on(table.DEVICE_TYPE_ID.eq(DeviceTypes.DEVICE_TYPES.ID))
                .fetch(this::basicMap);
    }
}