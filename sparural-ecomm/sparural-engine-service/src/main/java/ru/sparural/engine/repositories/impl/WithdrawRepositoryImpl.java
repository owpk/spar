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
                .set(table.CHECK_ID, entity.getCheckId())
                .set(table.WITHDRAW_TYPE, entity.getWithdrawType())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CURRENCY_ID, entity.getCurrencyId())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .onConflict(table.CHECK_ID, table.CURRENCY_ID)
                .doUpdate()
                .set(table.WITHDRAW_TYPE, entity.getWithdrawType())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Withdraw.class);

    }

    @Override
    public List<Withdraw> getListByCheckId(Long checkId) {
        return dslContext
                .selectFrom(table)
                .where(table.CHECK_ID.eq(checkId))
                .orderBy(table.ID.desc())
                .fetch()
                .into(Withdraw.class);
    }

    @Override
    public List<Withdraw> batchSave(List<Withdraw> list) {
        var insert = dslContext
                .insertInto(table,
                        table.CHECK_ID,
                        table.WITHDRAW_TYPE,
                        table.DESCRIPTION,
                        table.AMOUNT,
                        table.CURRENCY_ID,
                        table.CREATED_AT, table.UPDATED_AT);
        for (Withdraw entity : list) {
            insert = insert.values(entity.getCheckId(),
                    entity.getWithdrawType(),
                    entity.getDescription(),
                    entity.getAmount(),
                    entity.getCurrencyId(),
                    TimeHelper.currentTime(),
                    TimeHelper.currentTime());
        }
        return insert.returning()
                .fetch()
                .into(Withdraw.class);

    }


}
