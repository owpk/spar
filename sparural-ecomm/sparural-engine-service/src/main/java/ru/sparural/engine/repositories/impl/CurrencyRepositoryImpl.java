package ru.sparural.engine.repositories.impl;


import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.CurrencyEntity;
import ru.sparural.engine.repositories.CurrencyRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Currencies;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.coalesce;

@Service
@RequiredArgsConstructor
public class CurrencyRepositoryImpl implements CurrencyRepository {

    private final DSLContext dslContext;
    private final Currencies table = Currencies.CURRENCIES;
    Gson gson = new Gson();

    @Override
    public Optional<CurrencyEntity> getByExternalId(String externalId) {
        return dslContext
                .selectFrom(table)
                .where(table.EXTERNAL_ID.eq(externalId))
                .fetchOptionalInto(CurrencyEntity.class);
    }

    @Override
    public Optional<CurrencyEntity> saveOrUpdate(CurrencyEntity entity) {
        String nameCases = gson.toJson(entity.getNameCases());
        return dslContext
                .insertInto(table)
                .set(table.EXTERNAL_ID, entity.getExternalId())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.NAME, entity.getName())
                .set(table.NAME_CASES, nameCases)
                .set(table.IS_DELETED, entity.getIsDeleted())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.EXTERNAL_ID)
                .doUpdate()
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.NAME, entity.getName())
                .set(table.IS_DELETED, entity.getIsDeleted())
                .set(table.NAME_CASES, nameCases)
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(CurrencyEntity.class);
    }

    @Override
    public Optional<CurrencyEntity> update(CurrencyEntity entity) {
        String nameCases = gson.toJson(entity.getNameCases());
        return dslContext
                .update(table)
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.NAME, entity.getName())
                .set(table.IS_DELETED, entity.getIsDeleted())
                .set(table.NAME_CASES, nameCases)
                .set(table.IS_DELETED, entity.getIsDeleted())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .where(table.EXTERNAL_ID.eq(entity.getExternalId()))
                .returning()
                .fetchOptionalInto(CurrencyEntity.class);
    }

    public Optional<CurrencyEntity> get(Long id) {
        return dslContext
                .selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(CurrencyEntity.class);
    }

    @Override
    public List<CurrencyEntity> batchSave(List<CurrencyEntity> currencyEntityList) {
        var batchQueries = currencyEntityList.stream()
                .map(x -> {
                            var nameCases = gson.toJson(x.getNameCases());
                            return dslContext.insertInto(table)
                                    .set(table.NAME, x.getName())
                                    .set(table.IS_DELETED, x.getIsDeleted())
                                    .set(table.NAME_CASES, nameCases)
                                    .set(table.DESCRIPTION, x.getDescription())
                                    .set(table.EXTERNAL_ID, x.getExternalId())
                                    .set(table.CREATED_AT, TimeHelper.currentTime())
                                    .onConflict(table.EXTERNAL_ID)
                                    .doUpdate()
                                    .set(table.NAME, coalesce(table.as("excluded").NAME, table.NAME))
                                    .set(table.DESCRIPTION, coalesce(table.as("excluded").DESCRIPTION, table.DESCRIPTION))
                                    .set(table.IS_DELETED, coalesce(table.as("excluded").IS_DELETED, table.IS_DELETED))
                                    .set(table.UPDATED_AT, TimeHelper.currentTime());
                        }
                )
                .collect(Collectors.toList());
        dslContext.batch(batchQueries).execute();
        return dslContext.select().from(table).where(table.EXTERNAL_ID.in(currencyEntityList.stream()
                        .map(CurrencyEntity::getExternalId).collect(Collectors.toList())))
                .fetchInto(CurrencyEntity.class);
    }

    @Override
    public List<CurrencyEntity> fetchAll() {
        return fetchAll(null, null);
    }

    @Override
    public List<CurrencyEntity> fetchAll(Integer offset, Integer limit) {
        return dslContext.select().from(table).offset(offset).limit(limit)
                .fetchInto(CurrencyEntity.class);
    }

    @Override
    public List<CurrencyEntity> fetchByExternalIds(Set<String> extIds) {
        return dslContext.selectFrom(table).where(table.EXTERNAL_ID.in(extIds)).fetchInto(CurrencyEntity.class);
    }
}
