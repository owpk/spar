package ru.sparural.engine.services;

import ru.sparural.engine.entity.LoymaxCard;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface LoymaxCardService {

    void bindCard(Long cardId, Long loymaxCardId);

    LoymaxCard findByLocalCardId(Long id);

    void batchBindAsync(List<Long> cards, List<Long> loymaxCardsIds);
}
