package ru.sparural.engine.repositories.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.enums.UserFilterRegistrationTypes;
import ru.sparural.engine.entity.*;
import ru.sparural.engine.repositories.CheckRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Registrations;
import ru.sparural.tables.UsersGroupUser;
import ru.sparural.tables.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckRepositoryImpl implements CheckRepository {
    private final DSLContext dslContext;
    private final Checks table = Checks.CHECKS;
    private final LoymaxChecks loymaxTable = LoymaxChecks.LOYMAX_CHECKS;
    private final ChecksRewards rewardsTable = ChecksRewards.CHECKS_REWARDS;
    private final ChecksWithdraws withdrawsTable = ChecksWithdraws.CHECKS_WITHDRAWS;
    private final Currencies currencies = Currencies.CURRENCIES;
    private final ChecksItems itemsTable = ChecksItems.CHECKS_ITEMS;
    private final Merchants merchantsTable = Merchants.MERCHANTS;

    @Override
    public Optional<CheckEntity> get(Long id, Long cardId) {
        var result = dslContext.selectFrom(table)
                .where(table.CARD_ID.eq(id))
                .fetchInto(CheckEntity.class).get(0);
        return Optional.of(insertEntitiesToResult(result));
    }

    @Override
    public Optional<CheckEntity> saveOrUpdate(CheckEntity entity) {
        return dslContext
                .insertInto(table)
                .set(table.CARD_ID, entity.getCardId())
                .set(table.USER_ID, entity.getUserId())
                .set(table.MERCHANT_ID, entity.getMerchantId())
                .set(table.EXTERNAL_PURCHASE_ID, entity.getExternalPurchaseId())
                .set(table.DATE_TIME, entity.getDateTime())
                .set(table.IS_REFUND, entity.getIsRefund())
                .set(table.CHECK_NUMBER, String.valueOf(entity.getCheckNumber()))
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CURRENCY_ID, entity.getCurrencyId())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.EXTERNAL_PURCHASE_ID)
                .doUpdate()
                .set(table.CARD_ID, entity.getCardId())
                .set(table.USER_ID, entity.getUserId())
                .set(table.MERCHANT_ID, entity.getMerchantId())
                .set(table.DATE_TIME, entity.getDateTime())
                .set(table.IS_REFUND, entity.getIsRefund())
                .set(table.CHECK_NUMBER, String.valueOf(entity.getCheckNumber()))
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CURRENCY_ID, entity.getCurrencyId())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(CheckEntity.class);
    }

    public CheckEntity insertEntitiesToResult(CheckEntity result) {
        Long checkId = result.getId();

        List<Item> items = dslContext.selectFrom(ChecksItems.CHECKS_ITEMS)
                .where(ChecksItems.CHECKS_ITEMS.CHECK_ID.eq(checkId))
                .fetch().into(Item.class);
        result.setItems(items.stream().collect(Collectors.toMap(Item::getId, Function.identity())));

        List<Withdraw> withdraws = dslContext.selectFrom(ChecksWithdraws.CHECKS_WITHDRAWS)
                .where(ChecksWithdraws.CHECKS_WITHDRAWS.CHECK_ID.eq(checkId))
                .fetch().into(Withdraw.class);

        result.setWithdraws(withdraws.stream().collect(Collectors.toMap(Withdraw::getId, Function.identity())));

        List<Reward> rewards = dslContext.selectFrom(ChecksRewards.CHECKS_REWARDS)
                .where(ChecksRewards.CHECKS_REWARDS.CHECK_ID.eq(checkId))
                .fetch().into(Reward.class);

        result.setRewards(rewards.stream().collect(Collectors.toMap(Reward::getId, Function.identity())));

        return result;
    }

    @Override
    public void saveLoymaxChecks(Long checkId, String historyId) {
        dslContext
                .insertInto(loymaxTable)
                .set(loymaxTable.CHECK_ID, checkId)
                .set(loymaxTable.HISTORY_ID, historyId)
                .set(loymaxTable.CREATED_AT, TimeHelper.currentTime())
                .onConflict(loymaxTable.HISTORY_ID)
                .doNothing()
                .execute();
    }

    @Override
    public Optional<Long> getMerchantIdOfLastCheck(Long userId) {
        return dslContext.select(table.MERCHANT_ID)
                .from(table)
                .where(table.USER_ID.eq(userId).and(table.IS_NOTIF_SENT.eq(false)))
                .orderBy(table.DATE_TIME.desc())
                .limit(1)
                .fetchOptionalInto(Long.class);
    }

    @Override
    public Optional<CheckEntity> getById(Long id) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(CheckEntity.class);
    }

    @Override
    public Optional<Long> getCheckIdByLoymaxId(String id) {
        return dslContext.select(loymaxTable.CHECK_ID)
                .from(loymaxTable)
                .where(loymaxTable.HISTORY_ID.eq(id))
                .limit(1)
                .fetchOptionalInto(Long.class);
    }

    @Override
    public List<CheckEntity> getLastCheck(UserFilterDto filter, Long startTime) {
        var condition = DSL.noCondition();
        if (startTime != null && startTime != 0)
            condition = table.CREATED_AT.greaterOrEqual(startTime / 1000);
        var lastChecks = dslContext.select(
                        table.USER_ID.as("user_id"),
                        DSL.max(table.ID).as("check_id")
                ).from(table)
                .where(condition)
                .groupBy(table.USER_ID)
                .asTable();

        var query = dslContext.select(table.fields())
                .from(lastChecks)
                .leftJoin(table)
                .on(lastChecks.field("check_id", Long.class).eq(table.ID))
                .leftJoin(Merchants.MERCHANTS)
                .on(table.MERCHANT_ID.eq(Merchants.MERCHANTS.ID));

        List<Condition> conditions = new LinkedList<>();
        conditions.add(table.IS_NOTIF_SENT.eq(Boolean.FALSE));
        //add tables
        if (filter != null) {

            if (!CollectionUtils.isEmpty(filter.getUserIds())) {
                query = query.leftJoin(Users.USERS)
                        .on(table.USER_ID.eq(Users.USERS.ID));
                conditions.add(Users.USERS.ID.in(filter.getUserIds()));
            }


            if (!CollectionUtils.isEmpty(filter.getGroupIds())) {
                query = query.leftJoin(UsersGroupUser.USERS_GROUP_USER)
                        .on(table.USER_ID.eq(UsersGroupUser.USERS_GROUP_USER.USER_ID));
                conditions.add(UsersGroupUser.USERS_GROUP_USER.USERS_GROUP_ID.in(filter.getGroupIds()));
            }


            if (UserFilterRegistrationTypes.isNeedFilter(filter.getRegistrationType())) {
                query = query.leftJoin(Registrations.REGISTRATIONS)
                        .on(table.USER_ID.eq(Registrations.REGISTRATIONS.USER_ID));
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
                    .set(table.IS_NOTIF_SENT, true)
                    .where(table.ID.in(part))
                    .execute();
        });
    }

    @Override
    public List<CheckEntity> getAllChecksByUserId(Long x) {
        return dslContext
                .selectFrom(table)
                .where(table.USER_ID.eq(x))
                .fetch()
                .into(CheckEntity.class);
    }

    @Override
    public List<CheckEntity> batchSaveOrUpdate(List<CheckEntity> checkEntities) {
        var insert = dslContext
                .insertInto(
                        table,
                        table.CARD_ID,
                        table.USER_ID,
                        table.MERCHANT_ID,
                        table.EXTERNAL_PURCHASE_ID,
                        table.DATE_TIME,
                        table.IS_REFUND,
                        table.CHECK_NUMBER,
                        table.AMOUNT,
                        table.CURRENCY_ID,
                        table.CREATED_AT);
        for (CheckEntity entity : checkEntities)
            insert = insert.values(
                    entity.getCardId(),
                    entity.getUserId(),
                    entity.getMerchantId(),
                    entity.getExternalPurchaseId(),
                    entity.getDateTime(),
                    entity.getIsRefund(),
                    String.valueOf(entity.getCheckNumber()),
                    entity.getAmount(),
                    entity.getCurrencyId(),
                    TimeHelper.currentTime()
            );

        return insert
                .onConflict(table.EXTERNAL_PURCHASE_ID)
                .doUpdate()
                .set(table.CARD_ID, DSL.coalesce(table.as("excluded").CARD_ID, table.CARD_ID))
                .set(table.USER_ID, DSL.coalesce(table.as("excluded").USER_ID, table.USER_ID))
                .set(table.MERCHANT_ID, DSL.coalesce(table.as("excluded").MERCHANT_ID, table.MERCHANT_ID))
                .set(table.DATE_TIME, DSL.coalesce(table.as("excluded").DATE_TIME, table.DATE_TIME))
                .set(table.IS_REFUND, DSL.coalesce(table.as("excluded").IS_NOTIF_SENT, table.IS_NOTIF_SENT))
                .set(table.CHECK_NUMBER, DSL.coalesce(table.as("excluded").CHECK_NUMBER, table.CHECK_NUMBER))
                .set(table.AMOUNT, DSL.coalesce(table.as("excluded").AMOUNT, table.AMOUNT))
                .set(table.CURRENCY_ID, DSL.coalesce(table.as("excluded").CURRENCY_ID, table.CURRENCY_ID))
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetch()
                .into(CheckEntity.class);
    }

    @Override
    public List<CheckEntity> fetchAllByLoymaxIds(List<String> loymaxIds, Map<Long, CurrencyEntity> currencyEntities) {
        var selectOnWhere = basicSelect().where(table.EXTERNAL_PURCHASE_ID.in(loymaxIds));
        Map<UUID, CheckEntity> checks = new HashMap<>();
        selectOnWhere
                .fetch()
                .forEach(record -> computeRecordToCheckEntity(record, checks, currencyEntities));
        return new ArrayList<>(checks.values());
    }

    private void computeRecordToCheckEntity(Record record, Map<UUID, CheckEntity> checks,
                                            Map<Long, CurrencyEntity> currencies) {
        UUID checkId = UUID.fromString(record.get(table.EXTERNAL_PURCHASE_ID));
        var currentCheck = checks.computeIfAbsent(checkId, k -> mapRecordToCheckEntity(record));
        currentCheck.setCurrency(currencies.get(currentCheck.getCurrencyId()));

        if (record.get(itemsTable.ID) != null)
            currentCheck.getItems().computeIfAbsent(itemsTable.ID.get(record),
                    r -> record.into(itemsTable.fields()).into(Item.class));

        if (record.get(withdrawsTable.ID) != null)
            currentCheck.getWithdraws().computeIfAbsent(withdrawsTable.ID.get(record),
                    r -> record.into(withdrawsTable.fields()).into(Withdraw.class));

        if (record.get(rewardsTable.ID) != null)
            currentCheck.getRewards().computeIfAbsent(rewardsTable.ID.get(record),
                    r -> record.into(rewardsTable.fields()).into(Reward.class));
    }

    private CheckEntity mapRecordToCheckEntity(Record record) {
        var check = record.into(table.fields()).into(CheckEntity.class);
        var merchant = record.into(merchantsTable.fields()).into(Merchant.class);
        check.setMerchant(merchant);
        return check;
    }

    private SelectOnConditionStep<?> basicSelect() {
        return dslContext.select().from(table)
                .leftJoin(merchantsTable)
                .on(merchantsTable.ID.eq(table.MERCHANT_ID))
                .leftJoin(itemsTable)
                .on(itemsTable.CHECK_ID.eq(table.ID))
                .leftJoin(rewardsTable)
                .on(rewardsTable.CHECK_ID.eq(table.ID))
                .leftJoin(withdrawsTable)
                .on(withdrawsTable.CHECK_ID.eq(table.ID));
    }
}