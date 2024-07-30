package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Reward;
import ru.sparural.engine.repositories.RewardRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.ChecksRewards;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RewardRepositoryImpl implements RewardRepository {

    private final DSLContext dslContext;
    private final ChecksRewards table = ChecksRewards.CHECKS_REWARDS;

    @Override
    public Optional<Reward> saveOrUpdate(Reward entity) {
        return dslContext
                .insertInto(table)
                .set(table.CHECKID, entity.getCheckId())
                .set(table.REWARDTYPE, entity.getRewardType())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CURRENCYID, entity.getCurrenciesId())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Reward.class);

    }

    @Override
    public List<Reward> getListByCheckId(Long checkId) {
        return dslContext
                .selectFrom(table)
                .where(table.CHECKID.eq(checkId))
                .fetch()
                .into(Reward.class);
    }

    @Override
    public List<Reward> batchSave(List<Reward> list) {
        var insert = dslContext
                .insertInto(table,
                        table.CHECKID,
                        table.REWARDTYPE,
                        table.DESCRIPTION, table.AMOUNT, table.CURRENCYID, table.CREATEDAT);
        for (Reward entity : list) {
            insert = insert.values(entity.getCheckId(),
                    entity.getRewardType(),
                    entity.getDescription(),
                    entity.getAmount(),
                    entity.getCurrenciesId(),
                    TimeHelper.currentTime());
        }
        return insert
                .returning()
                .fetch()
                .into(Reward.class);
    }
}
