package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.LoymaxCard;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface LoymaxCardRepository {
    void save(Long cardId, Long loymaxCardId);

    Optional<LoymaxCard> findByLocalCardId(Long id);

    void batchBindToLoymaxCard(List<Long> cards, List<Long> loymaxCardsIds);
}
