package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.Card;
import ru.sparural.engine.entity.CheckIdentityCardId;
import ru.sparural.engine.repositories.CardRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.CardUser;
import ru.sparural.tables.Cards;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepository {

    private final DSLContext dslContext;
    private final Cards table = Cards.CARDS;

    @Override
    public Optional<Card> findByNumber(String number) {
        return dslContext.select()
                .from(table)
                .where(table.NUMBER.eq(number))
                .fetchOptionalInto(Card.class);
    }

    @Transactional
    @Override
    public Optional<Card> saveOrUpdate(Card card) {
        return dslContext.insertInto(table)
                .set(table.BAR_CODE, card.getBarCode())
                .set(table.NUMBER, card.getNumber())
                .set(table.BLOCK, card.getBlock())
                .set(table.OWNER_ID, card.getOwnerId())
                .set(table.EXPIRY_DATE, card.getExpiryDate())
                .set(table.STATUS, card.getStatus().getVal())
                .set(table.CARD_TYPE, card.getCardType())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.NUMBER)
                .doUpdate()
                .set(table.BAR_CODE, card.getBarCode())
                .set(table.BLOCK, card.getBlock())
                .set(table.OWNER_ID, card.getOwnerId())
                .set(table.EXPIRY_DATE, card.getExpiryDate())
                .set(table.STATUS, card.getStatus().getVal())
                .set(table.CARD_TYPE, card.getCardType())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Card.class);
    }

    @Transactional
    @Override
    public void createUserCardRecord(Long cardId, Long userId, boolean canBlock, boolean canReplace) {
        ru.sparural.tables.CardUser table = ru.sparural.tables.CardUser.CARD_USER;
        dslContext.insertInto(table)
                .set(table.USER_ID, userId)
                .set(table.CARD_ID, cardId)
                .set(table.CAN_REPLACE, canReplace)
                .set(table.CAN_BLOCK, canBlock)
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.USER_ID, table.CARD_ID)
                .doNothing().execute();
    }

    @Transactional
    @Override
    public List<Card> batchSaveOrUpdate(List<Card> entities) {

        var insert =
                dslContext.insertInto(table, table.BAR_CODE, table.BLOCK,
                        table.EXPIRY_DATE, table.NUMBER,
                        table.STATUS, table.OWNER_ID, table.CARD_TYPE,
                        table.CREATED_AT);

        for (var rec : entities)
            insert = insert.values(rec.getBarCode(),
                    rec.getBlock(),
                    rec.getExpiryDate(),
                    rec.getNumber(),
                    rec.getStatus().getVal(),
                    rec.getOwnerId(),
                    rec.getCardType(),
                    TimeHelper.currentTime()
            );

        var insertStep = insert.onConflict(table.NUMBER)
                .doUpdate()
                .set(table.BAR_CODE, DSL.coalesce(table.as("excluded").BAR_CODE, table.BAR_CODE))
                .set(table.STATUS, DSL.coalesce(table.as("excluded").STATUS, table.STATUS))
                .set(table.EXPIRY_DATE, DSL.coalesce(table.as("excluded").EXPIRY_DATE, table.EXPIRY_DATE))
                .set(table.OWNER_ID, DSL.coalesce(table.as("excluded").OWNER_ID, table.OWNER_ID))
                .set(table.BLOCK, DSL.coalesce(table.as("excluded").BLOCK, table.BLOCK))
                .set(table.NUMBER, DSL.coalesce(table.as("excluded").NUMBER, table.NUMBER))
                .set(table.CARD_TYPE, DSL.coalesce(table.as("excluded").CARD_TYPE, table.CARD_TYPE))
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returningResult(Cards.CARDS.fields());

        return insertStep
                .fetch()
                .into(Card.class);
    }

    @Transactional
    @Override
    public void batchBind(List<Long> ids, Long userId, boolean canBlock, boolean canReplace) {
        var table = CardUser.CARD_USER;
        var insert =
                dslContext.insertInto(table, table.CAN_BLOCK, table.CAN_REPLACE,
                        table.USER_ID, table.CARD_ID, table.CREATED_AT);

        for (var id : ids)
            insert = insert.values(canBlock, canReplace,
                    userId, id, TimeHelper.currentTime());

        insert.onConflict(table.CARD_ID, table.USER_ID).
                doUpdate()
                .set(table.CAN_BLOCK, canBlock)
                .set(table.CAN_REPLACE, canReplace)
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public List<Card> fetch(Long offset, Long limit, Long userId) {
        return dslContext.select().from(table)
                .leftJoin(CardUser.CARD_USER)
                .on(table.ID.eq(CardUser.CARD_USER.CARD_ID)
                        .and(CardUser.CARD_USER.USER_ID.eq(userId)))
                .orderBy(table.ID.desc())
                .limit(limit)
                .offset(offset)
                .fetch().into(Card.class);
    }

    @Override
    public Optional<Long> findByCardIdByUserId(Long userId) {
        return dslContext.select(table.ID)
                .from(table)
                .where(table.OWNER_ID.eq(userId))
                .limit(1)
                .fetchOptionalInto(Long.class);
    }

    @Override
    public List<CheckIdentityCardId> findAllByNumbers(Set<String> identities, Long userId) {
        var cu = CardUser.CARD_USER;
        return dslContext.select(
                        table.ID.as("cardId"),
                        table.NUMBER.as("identity"))
                .from(table)
                .leftJoin(cu)
                .on(table.ID.eq(cu.CARD_ID))
                .where(table.NUMBER.in(identities).and(cu.USER_ID.eq(userId)))
                .fetchInto(CheckIdentityCardId.class);
    }

}