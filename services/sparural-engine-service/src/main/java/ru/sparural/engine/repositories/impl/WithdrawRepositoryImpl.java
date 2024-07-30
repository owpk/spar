package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Withdraw;
import ru.sparural.engine.repositories.WithdrawRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.ChecksWithdraws;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawRepositoryImpl implements WithdrawRepository {

    private final DSLContext dslContext;
    private final ChecksWithdraws table = ChecksWithdraws.CHECKS_WITHDRAWS;

    @Override
    public Optional<Withdraw> saveOrUpdate(Withdraw entity) {
        return dslContext
                .insertInto(table)
                .set(table.CHECKID, entity.getCheckId())
                .set(table.WITHDRAWTYPE, entity.getWithdrawType())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CURRENCYID, entity.getCurrenciesId())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .onConflict(table.CHECKID, table.CURRENCYID)
                .doUpdate()
                .set(table.WITHDRAWTYPE, entity.getWithdrawType())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Withdraw.class);

    }

    @Override
    public List<Withdraw> getListByCheckId(Long checkId) {
        return dslContext
                .selectFrom(table)
                .where(table.CHECKID.eq(checkId))
                .orderBy(table.ID.desc())
                .fetch()
                .into(Withdraw.class);
    }

    @Override
    public List<Withdraw> batchSave(List<Withdraw> list) {
        var insert = dslContext
                .insertInto(table,
                        table.CHECKID,
                        table.WITHDRAWTYPE,
                        table.DESCRIPTION,
                        table.AMOUNT,
                        table.CURRENCYID,
                        table.CREATEDAT, table.UPDATEDAT);
        for (Withdraw entity : list) {
            insert = insert.values(entity.getCheckId(),
                    entity.getWithdrawType(),
                    entity.getDescription(),
                    entity.getAmount(),
                    entity.getCurrenciesId(),
                    TimeHelper.currentTime(),
                    TimeHelper.currentTime());
        }
        return insert
                .onConflict(table.CHECKID, table.CURRENCYID)
                .doNothing()
                .returning()
                .fetch()
                .into(Withdraw.class);

    }


}
