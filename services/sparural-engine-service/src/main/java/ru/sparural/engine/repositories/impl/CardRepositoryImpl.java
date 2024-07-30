package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.Card;
import ru.sparural.engine.repositories.CardRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.CardUser;
import ru.sparural.tables.Cards;

import java.util.List;
import java.util.Optional;

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
                .set(table.BARCODE, card.getBarCode())
                .set(table.NUMBER, card.getNumber())
                .set(table.BLOCK, card.getBlock())
                .set(table.OWNERID, card.getOwnerId())
                .set(table.EXPIRYDATE, card.getExpiryDate())
                .set(table.STATUS, card.getStatus())
                .set(table.CARDTYPE, card.getCardType())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.NUMBER)
                .doUpdate()
                .set(table.BARCODE, card.getBarCode())
                .set(table.BLOCK, card.getBlock())
                .set(table.OWNERID, card.getOwnerId())
                .set(table.EXPIRYDATE, card.getExpiryDate())
                .set(table.STATUS, card.getStatus())
                .set(table.CARDTYPE, card.getCardType())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Card.class);
    }

    @Transactional
    @Override
    public void createUserCardRecord(Long cardId, Long userId, boolean canBlock, boolean canReplace) {
        ru.sparural.tables.CardUser table = ru.sparural.tables.CardUser.CARD_USER;
        dslContext.insertInto(table)
                .set(table.USERID, userId)
                .set(table.CARDID, cardId)
                .set(table.CANREPLACE, canReplace)
                .set(table.CANBLOCK, canBlock)
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.USERID, table.CARDID)
                .doNothing().execute();
    }

    @Transactional
    @Override
    public List<Card> batchSaveOrUpdate(List<Card> entities) {

        var insert =
                dslContext.insertInto(table, table.BARCODE, table.BLOCK,
                        table.EXPIRYDATE, table.NUMBER,
                        table.STATUS, table.OWNERID, table.CARDTYPE,
                        table.CREATEDAT);

        for (var rec : entities)
            insert = insert.values(rec.getBarCode(),
                    rec.getBlock(),
                    rec.getExpiryDate(),
                    rec.getNumber(),
                    rec.getStatus(),
                    rec.getOwnerId(),
                    rec.getCardType(),
                    TimeHelper.currentTime()
            );

        var insertStep = insert.onConflict(table.NUMBER)
                .doUpdate()
                .set(table.BARCODE, DSL.coalesce(table.as("excluded").BARCODE, table.BARCODE))
                .set(table.STATUS, DSL.coalesce(table.as("excluded").STATUS, table.STATUS))
                .set(table.EXPIRYDATE, DSL.coalesce(table.as("excluded").EXPIRYDATE, table.EXPIRYDATE))
                .set(table.OWNERID, DSL.coalesce(table.as("excluded").OWNERID, table.OWNERID))
                .set(table.BLOCK, DSL.coalesce(table.as("excluded").BLOCK, table.BLOCK))
                .set(table.NUMBER, DSL.coalesce(table.as("excluded").NUMBER, table.NUMBER))
                .set(table.CARDTYPE, DSL.coalesce(table.as("excluded").CARDTYPE, table.CARDTYPE))
                .set(table.UPDATEDAT, TimeHelper.currentTime())
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
                dslContext.insertInto(table, table.CANBLOCK, table.CANREPLACE,
                        table.USERID, table.CARDID, table.CREATEDAT);

        for (var id : ids)
            insert = insert.values(canBlock, canReplace,
                    userId, id, TimeHelper.currentTime());

        insert.onConflict(table.CARDID, table.USERID).
                doUpdate()
                .set(table.CANBLOCK, canBlock)
                .set(table.CANREPLACE, canReplace)
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public List<Card> fetch(Long offset, Long limit, Long userId) {
        return dslContext.select().from(table)
                .leftJoin(CardUser.CARD_USER)
                .on(table.ID.eq(CardUser.CARD_USER.CARDID)
                        .and(CardUser.CARD_USER.USERID.eq(userId)))
                .orderBy(table.ID.desc())
                .limit(limit)
                .offset(offset)
                .fetch().into(Card.class);
    }

    @Override
    public Optional<Long> findByCardIdByUserId(Long userId) {
        return dslContext.select(table.ID)
                .from(table)
                .where(table.OWNERID.eq(userId))
                .limit(1)
                .fetchOptionalInto(Long.class);
    }

}