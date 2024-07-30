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
                .where(table.EXTERNALID.eq(externalId))
                .fetchOptionalInto(CurrencyEntity.class);
    }

    @Override
    public Optional<CurrencyEntity> saveOrUpdate(CurrencyEntity entity) {
        String nameCases = gson.toJson(entity.getNameCases());
        return dslContext
                .insertInto(table)
                .set(table.EXTERNALID, entity.getExternalId())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.NAME, entity.getName())
                .set(table.NAMECASES, nameCases)
                .set(table.ISDELETED, entity.getIsDeleted())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.EXTERNALID)
                .doUpdate()
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.NAME, entity.getName())
                .set(table.ISDELETED, entity.getIsDeleted())
                .set(table.NAMECASES, nameCases)
                .set(table.UPDATEDAT, TimeHelper.currentTime())
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
                .set(table.ISDELETED, entity.getIsDeleted())
                .set(table.NAMECASES, nameCases)
                .set(table.ISDELETED, entity.getIsDeleted())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .where(table.EXTERNALID.eq(entity.getExternalId()))
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
        var insert = dslContext
                .insertInto(table, table.EXTERNALID, table.DESCRIPTION, table.NAME, table.NAMECASES, table.ISDELETED,
                        table.CREATEDAT);
        for (CurrencyEntity entity : currencyEntityList) {
            String nameCases = gson.toJson(entity.getNameCases());
            insert = insert
                    .values(entity.getExternalId(),
                            entity.getDescription(),
                            entity.getName(),
                            nameCases,
                            entity.getIsDeleted(),
                            TimeHelper.currentTime());
            insert
                    .onConflict(table.EXTERNALID)
                    .doNothing();
        }
        return insert
                .returning()
                .fetch()
                .into(CurrencyEntity.class);
    }
}
