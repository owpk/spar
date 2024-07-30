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
                .set(table.CARD_ID, cardId)
                .set(table.LOYMAX_CARD_ID, loymaxCardId)
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.LOYMAX_CARD_ID, table.CARD_ID)
                .doUpdate()
                .set(table.LOYMAX_CARD_ID, loymaxCardId)
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public Optional<LoymaxCard> findByLocalCardId(Long id) {
        return dslContext.select().from(LoymaxCards.LOYMAX_CARDS)
                .where(LoymaxCards.LOYMAX_CARDS.CARD_ID.eq(id))
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
            loymaxCards.setCardId(card);
            loymaxCards.setLoymaxCardId(loymaxCardId);
            pojos.add(loymaxCards);
        }

        var insert =
                dslContext.insertInto(table, table.CARD_ID, table.LOYMAX_CARD_ID, table.CREATED_AT);

        for (var pojo : pojos)
            insert = insert.values(pojo.getCardId(), pojo.getLoymaxCardId(), TimeHelper.currentTime());

        insert.onConflict(table.CARD_ID, table.LOYMAX_CARD_ID)
                .doNothing().executeAsync();
    }
}