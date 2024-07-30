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
                .set(table.CHECK_ID, entity.getCheckId())
                .set(table.REWARD_TYPE, entity.getRewardType())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CURRENCY_ID, entity.getCurrencyId())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Reward.class);

    }

    @Override
    public List<Reward> getListByCheckId(Long checkId) {
        return dslContext
                .selectFrom(table)
                .where(table.CHECK_ID.eq(checkId))
                .fetch()
                .into(Reward.class);
    }

    @Override
    public List<Reward> batchSave(List<Reward> list) {
        var insert = dslContext
                .insertInto(table,
                        table.CHECK_ID,
                        table.REWARD_TYPE,
                        table.DESCRIPTION, table.AMOUNT, table.CURRENCY_ID, table.CREATED_AT);
        for (Reward entity : list) {
            insert = insert.values(entity.getCheckId(),
                    entity.getRewardType(),
                    entity.getDescription(),
                    entity.getAmount(),
                    entity.getCurrencyId(),
                    TimeHelper.currentTime());
        }
        return insert
                .returning()
                .fetch()
                .into(Reward.class);
    }
}
