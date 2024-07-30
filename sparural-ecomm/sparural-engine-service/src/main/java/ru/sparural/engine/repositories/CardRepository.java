package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Card;
import ru.sparural.engine.entity.CheckIdentityCardId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Vorobyev Vyacheslav
 */
public interface CardRepository {

    Optional<Card> findByNumber(String number);

    Optional<Card> saveOrUpdate(Card card);

    void createUserCardRecord(Long cardId, Long userId, boolean canBlock, boolean canReplace);

    List<Card> batchSaveOrUpdate(List<Card> entities);

    void batchBind(List<Long> ids, Long userId, boolean canBlock, boolean canReplace);

    List<Card> fetch(Long offset, Long limit, Long userId);

    Optional<Long> findByCardIdByUserId(Long userId);

    List<CheckIdentityCardId> findAllByNumbers(Set<String> identities, Long userId);
}
