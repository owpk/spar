package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.LoymaxCard;
import ru.sparural.engine.repositories.LoymaxCardRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.LoymaxCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class LoymaxCardRepositoryImpl implements LoymaxCardRepository {

    private final DSLContext dslContext;
    private final LoymaxCards table = LoymaxCards.LOYMAX_CARDS;

    @Override
    public void save(Long cardId, Long loymaxCardId) {
        dslContext.insertInto(table)
                .set(table.CARDID, cardId)
                .set(table.LOYMAXCARDID, loymaxCardId)
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.LOYMAXCARDID, table.CARDID)
                .doUpdate()
                .set(table.LOYMAXCARDID, loymaxCardId)
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public Optional<LoymaxCard> findByLocalCardId(Long id) {
        return dslContext.select().from(LoymaxCards.LOYMAX_CARDS)
                .where(LoymaxCards.LOYMAX_CARDS.CARDID.eq(id))
                .fetchOptionalInto(LoymaxCard.class);
    }

    @Transactional
    @Override
    public void batchBindToLoymaxCard(List<Long> cards, List<Long> loymaxCardsIds) {
        if (cards.size() != loymaxCardsIds.size())
            throw new IllegalStateException("Bind arrays not equal");
        List<ru.sparural.tables.pojos.LoymaxCards> pojos = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            var card = cards.get(i);
            var loymaxCardId = loymaxCardsIds.get(i);
            ru.sparural.tables.pojos.LoymaxCards loymaxCards = new ru.sparural.tables.pojos.LoymaxCards();
            loymaxCards.setCardid(card);
            loymaxCards.setLoymaxcardid(loymaxCardId);
            pojos.add(loymaxCards);
        }

        var insert =
                dslContext.insertInto(table, table.CARDID, table.LOYMAXCARDID, table.CREATEDAT);

        for (var pojo : pojos)
            insert = insert.values(pojo.getCardid(), pojo.getLoymaxcardid(), TimeHelper.currentTime());

        insert.onConflict(table.CARDID, table.LOYMAXCARDID)
                .doNothing().executeAsync();
    }
}