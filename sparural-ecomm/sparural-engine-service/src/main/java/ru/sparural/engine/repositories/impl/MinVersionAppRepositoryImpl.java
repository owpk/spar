package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.MinVersionAppEntity;
import ru.sparural.engine.repositories.MinVersionAppRepository;
import ru.sparural.tables.MinVersionApp;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class MinVersionAppRepositoryImpl implements MinVersionAppRepository {
    private final DSLContext dslContext;

    @Override
    public List<MinVersionAppEntity> getALl() {
        return dslContext.selectFrom(MinVersionApp.MIN_VERSION_APP)
                .fetch().into(MinVersionAppEntity.class);
    }

    @Override
    public Optional<MinVersionAppEntity> getLast() {
        return dslContext.selectFrom(MinVersionApp.MIN_VERSION_APP)
                .orderBy(MinVersionApp.MIN_VERSION_APP.CREATED_AT.desc())
                .limit(1)
                .fetchOptionalInto(MinVersionAppEntity.class);
    }

    @Override
    public Optional<MinVersionAppEntity> create(MinVersionAppEntity minVersionAppEntity) {
        var table = MinVersionApp.MIN_VERSION_APP;
        return dslContext.insertInto(table)
                .set(table.MIN_VERSION_APP_, minVersionAppEntity.getMinVersionApp())
                .set(table.CREATED_AT, new Date().getTime())
                .returningResult().fetchOptionalInto(MinVersionAppEntity.class);
    }
}
