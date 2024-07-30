package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.cards.UserCardsAccountsDto;
import ru.sparural.engine.entity.Card;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Vorobyev Vyacheslav
 */
public interface CardsService {
    Card saveOrUpdate(Card card);

    void bind(Long cardId, Long userId, boolean canBlock, boolean canReplace);

    Card createEntityFromDto(UserCardDto card, Long userId);

    UserCardDto createDtoFromEntity(Card card, Long userId);

    boolean checkIfCardExists(String number);

    List<Card> batchSaveOrUpdate(List<Card> entities);

    void batchBind(List<Long> entities, Long userId, boolean canBlock, boolean canReplace);

    List<Card> fetch(Long offset, Long limit, Long userId);

    UserCardDto findByNumber(String number, Long userId);

    Long findCardIdByUserId(Long userId);

    List<UserCardDto> selectAndBindUserCards(Long userId);

    UserCardsAccountsDto selectCardsAndAccounts(Long userId) throws ExecutionException, InterruptedException;

    void evictCardsCache(Long userId);
}