package ru.sparural.engine.repositories.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.enums.UserFilterRegistrationTypes;
import ru.sparural.engine.entity.CheckDBEntity;
import ru.sparural.engine.entity.CheckEntity;
import ru.sparural.engine.entity.Item;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.entity.Registrations;
import ru.sparural.engine.entity.Reward;
import ru.sparural.engine.entity.UsersGroupUser;
import ru.sparural.engine.entity.Withdraw;
import ru.sparural.engine.repositories.CheckRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckRepositoryImpl implements CheckRepository {
    private final DSLContext dslContext;
    private final Checks table = Checks.CHECKS;
    private final LoymaxChecks loymaxTable = LoymaxChecks.LOYMAX_CHECKS;

    @Override
    public Optional<CheckEntity> get(Long id, Long cardId) {
        var result = dslContext.selectFrom(table)
                .where(table.CARDID.eq(id))
                .fetchInto(CheckEntity.class).get(0);
        return Optional.of(insertEntitiesToResult(result));
    }

    @Override
    public Optional<CheckEntity> save(CheckEntity entity) {
        return dslContext
                .insertInto(table)
                .set(table.CARDID, entity.getCardId())
                .set(table.USERID, entity.getUserId())
                .set(table.MERCHANTID, entity.getMerchantsId())
                .set(table.EXTERNALPURCHASEID, entity.getExternalPurchaseId())
                .set(table.DATETIME, entity.getDateTime())
                .set(table.ISREFUND, entity.getIsRefund())
                .set(table.CHECKNUMBER, String.valueOf(entity.getCheckNumber()))
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CURRENCYID, entity.getCurrenciesId())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.EXTERNALPURCHASEID)
                .doNothing()
                .returning()
                .fetchOptionalInto(CheckEntity.class);
    }

    public CheckEntity insertEntitiesToResult(CheckEntity result) {
        Long checkId = result.getId();

        List<Item> items = dslContext.selectFrom(ChecksItems.CHECKS_ITEMS)
                .where(ChecksItems.CHECKS_ITEMS.CHECKID.eq(checkId))
                .fetch().into(Item.class);
        result.setItems(items);

        List<Withdraw> withdraws = dslContext.selectFrom(ChecksWithdraws.CHECKS_WITHDRAWS)
                .where(ChecksWithdraws.CHECKS_WITHDRAWS.CHECKID.eq(checkId))
                .fetch().into(Withdraw.class);

        result.setWithdraws(withdraws);

        List<Reward> rewards = dslContext.selectFrom(ChecksRewards.CHECKS_REWARDS)
                .where(ChecksRewards.CHECKS_REWARDS.CHECKID.eq(checkId))
                .fetch().into(Reward.class);

        result.setRewards(rewards);

        return result;
    }

    @Override
    public void saveLoymaxChecks(Long checkId, String historyId) {
        dslContext
                .insertInto(loymaxTable)
                .set(loymaxTable.CHECKID, checkId)
                .set(loymaxTable.HISTORYID, historyId)
                .set(loymaxTable.CREATEDAT, TimeHelper.currentTime())
                .onConflict(loymaxTable.HISTORYID)
                .doNothing()
                .execute();
    }

    @Override
    public List<LoymaxUser> getAllUserId() {
        return dslContext
                .select(LoymaxUsers.LOYMAX_USERS.USERID, LoymaxUsers.LOYMAX_USERS.LOYMAXUSERID)
                .from(LoymaxUsers.LOYMAX_USERS)
                .fetchInto(LoymaxUser.class);
    }

    @Override
    public Optional<Long> getMerchantIdOfLastCheck(Long userId) {
        return dslContext.select(table.MERCHANTID)
                .from(table)
                .where(table.USERID.eq(userId).and(table.ISNOTIFSENT.eq(false)))
                .orderBy(table.DATETIME.desc())
                .limit(1)
                .fetchOptionalInto(Long.class);
    }

    @Override
    public Optional<CheckDBEntity> getById(Long id) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(CheckDBEntity.class);
    }

    @Override
    public Optional<Long> getCheckIdByLoymaxId(String id) {
        return dslContext.select(loymaxTable.CHECKID)
                .from(loymaxTable)
                .where(loymaxTable.HISTORYID.eq(id))
                .limit(1)
                .fetchOptionalInto(Long.class);
    }

    @Override
    public List<CheckEntity> getLastCheck(UserFilterDto filter, Long startTime) {
        var condition = DSL.noCondition();
        if (startTime != null)
            condition = table.CREATEDAT.greaterOrEqual(startTime / 1000);
        var lastChecks = dslContext.select(
                        table.USERID.as("user_id"),
                        DSL.max(table.ID).as("check_id")
                ).from(table)
                .where(condition)
                .groupBy(table.USERID)
                .asTable();

        var query = dslContext.select(table.fields())
                .from(lastChecks)
                .leftJoin(table)
                .on(lastChecks.field("check_id", Long.class).eq(table.ID));

        List<Condition> conditions = new LinkedList<>();
        conditions.add(table.ISNOTIFSENT.eq(Boolean.FALSE));
        //add tables
        if (filter != null) {

            if (!CollectionUtils.isEmpty(filter.getUserIds())) {
                query = query.leftJoin(Users.USERS)
                        .on(table.USERID.eq(Users.USERS.ID));
                conditions.add(Users.USERS.ID.in(filter.getUserIds()));
            }


            if (!CollectionUtils.isEmpty(filter.getGroupIds())) {
                query = query.leftJoin(UsersGroupUser.USERS_GROUP_USER)
                        .on(table.USERID.eq(UsersGroupUser.USERS_GROUP_USER.USERID));
                conditions.add(UsersGroupUser.USERS_GROUP_USER.USERSGROUPID.in(filter.getGroupIds()));
            }


            if (UserFilterRegistrationTypes.isNeedFilter(filter.getRegistrationType())) {
                query = query.leftJoin(Registrations.REGISTRATIONS)
                        .on(table.USERID.eq(Registrations.REGISTRATIONS.USERID));
                switch (filter.getRegistrationType()) {
                    case REGISTRED:
                        conditions.add(Registrations.REGISTRATIONS.STEP.greaterOrEqual(5));
                        break;
                    case NO_REGISTRED:
                        conditions.add(Registrations.REGISTRATIONS.STEP.lessThan(5));
                        break;
                }
            }
        }

        return query.where(conditions)
                .fetchInto(CheckEntity.class);
    }

    @Override
    public void saveIsNotifCheck(List<Long> checkIds) {
        Lists.partition(checkIds, 1000).parallelStream().forEach(part -> {
            dslContext.update(table)
                    .set(table.ISNOTIFSENT, true)
                    .where(table.ID.in(part))
                    .execute();
        });
    }

    @Override
    public List<CheckEntity> getAllChecksByUserId(Long x) {
        return dslContext
                .selectFrom(table)
                .where(table.USERID.eq(x))
                .fetch()
                .into(CheckEntity.class);
    }

    @Override
    public List<CheckEntity> batchSaveOrUpdate(List<CheckEntity> checkEntities) {
        var insert = dslContext
                .insertInto(
                        table,
                        table.CARDID,
                        table.USERID,
                        table.MERCHANTID,
                        table.EXTERNALPURCHASEID,
                        table.DATETIME,
                        table.ISREFUND,
                        table.CHECKNUMBER,
                        table.AMOUNT,
                        table.CURRENCYID,
                        table.CREATEDAT);
        for (CheckEntity entity : checkEntities)
            insert = insert.values(
                    entity.getCardId(),
                    entity.getUserId(),
                    entity.getMerchantsId(),
                    entity.getExternalPurchaseId(),
                    entity.getDateTime(),
                    entity.getIsRefund(),
                    String.valueOf(entity.getCheckNumber()),
                    entity.getAmount(),
                    entity.getCurrenciesId(),
                    TimeHelper.currentTime()
            );

        return insert
                .onConflict(table.EXTERNALPURCHASEID)
                .doUpdate()
                .set(table.CARDID, DSL.coalesce(table.as("excluded").CARDID, table.CARDID))
                .set(table.USERID, DSL.coalesce(table.as("excluded").USERID, table.USERID))
                .set(table.MERCHANTID, DSL.coalesce(table.as("excluded").MERCHANTID, table.MERCHANTID))
                .set(table.DATETIME, DSL.coalesce(table.as("excluded").DATETIME, table.DATETIME))
                .set(table.ISREFUND, DSL.coalesce(table.as("excluded").ISNOTIFSENT, table.ISNOTIFSENT))
                .set(table.CHECKNUMBER, DSL.coalesce(table.as("excluded").CHECKNUMBER, table.CHECKNUMBER))
                .set(table.AMOUNT, DSL.coalesce(table.as("excluded").AMOUNT, table.AMOUNT))
                .set(table.CURRENCYID, DSL.coalesce(table.as("excluded").CURRENCYID, table.CURRENCYID))
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returning()
                .fetch()
                .into(CheckEntity.class);
    }

}