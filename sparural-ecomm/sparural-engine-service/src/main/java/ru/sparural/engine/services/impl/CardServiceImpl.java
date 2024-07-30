package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.cards.UserCardsAccountsDto;
import ru.sparural.engine.api.dto.user.account.UserAccounts;
import ru.sparural.engine.entity.Card;
import ru.sparural.engine.entity.CheckIdentityCardId;
import ru.sparural.engine.entity.enums.CardStatuses;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.repositories.CardRepository;
import ru.sparural.engine.services.CardsService;
import ru.sparural.engine.services.LoymaxCardService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.CacheConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardsService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final CardRepository cardRepository;
    private final LoymaxCardService loymaxCardService;
    private final LoymaxService loymaxService;

    @Override
    public Card saveOrUpdate(Card card) {
        return cardRepository.saveOrUpdate(card)
                .orElseThrow(() -> new RuntimeException("Cannot save card"));
    }

    @Override
    public void bind(Long cardId, Long userId, boolean canBlock, boolean canReplace) {
        cardRepository.createUserCardRecord(cardId, userId, canBlock, canReplace);
    }

    @Override
    public Card createEntityFromDto(UserCardDto card, Long userId) {
        Card cardEntity = new Card();
        cardEntity.setId(card.getId());
        cardEntity.setBarCode(card.getBarCode());
        cardEntity.setBlock(card.getBlock());
        cardEntity.setNumber(card.getNumber());
        cardEntity.setExpiryDate(card.getExpiryDate());
        cardEntity.setOwnerId(userId);
        cardEntity.setStatus(CardStatuses.valueOf(card.getState()));
        cardEntity.setCardType(card.getCardType());
        return cardEntity;
    }

    @Override
    public UserCardDto createDtoFromEntity(Card card, Long userId) {
        UserCardDto userCardDto = new UserCardDto();
        userCardDto.setId(card.getId());
        userCardDto.setExpiryDate(card.getExpiryDate());
        userCardDto.setNumber(card.getNumber());
        userCardDto.setBarCode(card.getBarCode());
        userCardDto.setState(card.getStatus() != null ? card.getStatus().getVal() : null);
        userCardDto.setBlock(card.getBlock());
        userCardDto.setImOwner(card.getOwnerId() != null && card.getOwnerId().equals(userId));
        userCardDto.setCardType(card.getCardType());
        return userCardDto;
    }

    @Override
    public boolean checkIfCardExists(String number) {
        return cardRepository
                .findByNumber(number)
                .isPresent();
    }

    @Override
    public List<Card> batchSaveOrUpdate(List<Card> entities) {
        return cardRepository.batchSaveOrUpdate(entities);
    }

    @Override
    public void batchBind(List<Long> entities, Long userId, boolean canBlock, boolean canReplace) {
        cardRepository.batchBind(entities, userId, canBlock, canReplace);
    }

    @Override
    public List<Card> fetch(Long offset, Long limit, Long userId) {
        return cardRepository.fetch(offset, limit, userId);
    }

    @Override
    public UserCardDto findByNumber(String number, Long userId) {
        return cardRepository.findByNumber(number)
                .map(c -> createDtoFromEntity(c, userId))
                .orElse(null);
    }

    @Override
    public Long findCardIdByUserId(Long userId) {
        return cardRepository.findByCardIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User or card not found"));
    }

    @Override
    public List<UserCardDto> selectAndBindUserCards(Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        List<UserCardDto> cards = loymaxService.selectCards(loymaxUser);

        List<UserCardDto> isOwnerList = new ArrayList<>();
        List<UserCardDto> notOwnerList = new ArrayList<>();

        for (UserCardDto cardDto : cards)
            if (cardDto.getImOwner()) isOwnerList.add(cardDto);
            else notOwnerList.add(cardDto);

        var res1 = batchSaveOrUpdate(
                isOwnerList.stream().map(x -> createEntityFromDto(x, userId))
                        .collect(Collectors.toList()));
        var bindIdsOwner = res1.stream().map(Card::getId).collect(Collectors.toList());
        batchBind(bindIdsOwner, userId, true, true);

        List<Long> res = new ArrayList<>(bindIdsOwner);
        loymaxCardService.batchBindAsync(res, isOwnerList.stream().map(UserCardDto::getId).collect(Collectors.toList()));

        return res1.stream()
                .map(x -> createDtoFromEntity(x, userId))
                .collect(Collectors.toList());
    }

    @Override
    public UserCardsAccountsDto selectCardsAndAccounts(Long userId) throws ExecutionException, InterruptedException {
        var resultDto = new UserCardsAccountsDto();
        var setCardsTask = executorService.submit(() -> {
            var userCards = selectAndBindUserCards(userId);
            resultDto.setMyCards(userCards);
        });

        var setAccountTask = executorService.submit(() -> {
            try {
                var loymaxUser = loymaxService.getByLocalUserId(userId);
                loymaxService.refreshTokenIfNeeded(loymaxUser);
                var accounts = loymaxService
                        .getDetailedBalanceInfo(loymaxUser)
                        .stream()
                        .filter(x -> !x.getCurrency().getIsDeleted())
                        .collect(Collectors.toList());
                for (UserAccounts x : accounts) {
                    for (AccountsLifeTimesByTimeDTO y : x.getAccountLifeTimesByTime()) {
                        if (y.getAmount() != null) y.setAmount(Math.abs(y.getAmount()));
                    }
                }
                resultDto.setAccounts(accounts);
            } catch (Exception e) {
                log.error("Loymax select account task exception", e);
            }
        });
        setCardsTask.get();
        setAccountTask.get();
        return resultDto;
    }

    @Override
    @CacheEvict(cacheNames = CacheConstants.MOBILE_MAIN_SELECT_CARDS, key = "#userId")
    public void evictCardsCache(Long userId) {
        /* hack to evict cards cache */
    }

    @Override
    public List<CheckIdentityCardId> findAllByNumbers(Set<String> identities, Long userId) {
        return cardRepository.findAllByNumbers(identities, userId);
    }
}