package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.entity.Card;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.CardsService;
import ru.sparural.engine.services.LoymaxCardService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class FamilyCardController {

    private final LoymaxService loymaxService;
    private final CardsService cardsService;
    private final LoymaxCardService loymaxCardService;

    @KafkaSparuralMapping("family-cards/index")
    public List<UserCardDto> getList(@RequestParam Long userId,
                                     @RequestParam Long offset,
                                     @RequestParam Long limit) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        List<UserCardDto> cards = loymaxService.selectCards(loymaxUser);
        List<UserCardDto> cardNotOwner = cards
                .stream()
                .filter(x -> !x.getImOwner())
                .collect(Collectors.toList());

        log.info("Полученные карты юзера:" + cards);

        var entities = cards.stream()
                .filter(x -> !x.getImOwner())
                .map(x -> cardsService.createEntityFromDto(x, userId))
                .collect(Collectors.toList());


        var savedEntities = cardsService.batchSaveOrUpdate(entities);

        log.info(savedEntities.toString());
        var cardIds = savedEntities.stream().map(Card::getId)
                .collect(Collectors.toList());

        log.info(cardIds.toString());

        cardsService.batchBind(cardIds, userId, false, false);

        loymaxCardService.batchBindAsync(cardIds, cardNotOwner.stream().map(UserCardDto::getId).collect(Collectors.toList()));

        List<Card> fetchedCards = cardsService.fetch(offset, limit, userId);

        return savedEntities.stream()
                .map(x -> cardsService.createDtoFromEntity(x, userId))
                .collect(Collectors.toList());
    }
}
