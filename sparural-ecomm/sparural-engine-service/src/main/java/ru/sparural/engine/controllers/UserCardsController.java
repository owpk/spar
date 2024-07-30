package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.CodeDto;
import ru.sparural.engine.api.dto.cards.*;
import ru.sparural.engine.api.dto.screen.mycards.MyCardsInfoScreenDto;
import ru.sparural.engine.api.dto.screen.mycards.MyCardsInfoScreenDtoV3;
import ru.sparural.engine.loymax.rest.dto.cards.LoymaxUserCardRequestDto;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.CardQrService;
import ru.sparural.engine.services.CardsService;
import ru.sparural.engine.services.LoymaxCardService;
import ru.sparural.engine.services.MyCardsScreenService;
import ru.sparural.engine.services.exception.StatusException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.concurrent.ExecutionException;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class UserCardsController {

    private final LoymaxService loymaxService;
    private final CardsService cardsService;
    private final CardQrService cardQrService;
    private final LoymaxCardService loymaxCardService;
    private final MyCardsScreenService myCardsScreenService;

    @KafkaSparuralMapping("cards/attach")
    public Boolean attach(@Payload CardNumberPasswordRequestDto userCardDto,
                          @RequestParam Long userId) {
        var loymaxUserCardDto = new LoymaxUserCardRequestDto();
        loymaxUserCardDto.setCardNumber(userCardDto.getNumber());
        loymaxUserCardDto.setPassword(userCardDto.getPassword());

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.attachUserCard(loymaxUser, loymaxUserCardDto);
        return true;
    }

    // TODO check 'imOwner' business logic
    @KafkaSparuralMapping("cards/create")
    public UserCardDto create(@Payload CardNumberRequestDto cardNumberRequestDto,
                              @RequestParam Long userId) {
        if (cardsService.checkIfCardExists(cardNumberRequestDto.getNumber()))
            throw new StatusException("You already have bonus card", 403);
        var loymaxCardReq = new LoymaxUserCardRequestDto();
        loymaxCardReq.setCardNumber(cardNumberRequestDto.getNumber());

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var card = loymaxService.createUserCard(loymaxUser, loymaxCardReq);

        var savedCard = cardsService.saveOrUpdate(cardsService.createEntityFromDto(card, userId));
        cardsService.bind(savedCard.getId(), userId, true, true);
        loymaxCardService.bindCard(savedCard.getId(), card.getId());
        return card;
    }

    @KafkaSparuralMapping("my-cards/v3")
    public MyCardsInfoScreenDtoV3 getMyCardsScreenV3(@RequestParam Long userId) throws ExecutionException, InterruptedException {
        return myCardsScreenService.getMyCardsScreenV3(userId);
    }

    @KafkaSparuralMapping("my-cards/v2")
    @Deprecated
    public MyCardsInfoScreenDto getMyCardsScreenV2(@RequestParam Long userId) throws ExecutionException, InterruptedException {
        return myCardsScreenService.getMyCardsScreenV2(userId);
    }

    @Deprecated
    @KafkaSparuralMapping("my-cards/v1")
    public MyCardsInfoScreenDto getMyCardsScreenV1(@RequestParam Long userId) throws ExecutionException, InterruptedException {
        return myCardsScreenService.getMyCardsScreenV1(userId);
    }

    @KafkaSparuralMapping("cards/emit-virtual")
    public UserCardDto emitVirtual(@RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var card = loymaxService.emitVirtualCard(loymaxUser);
        cardsService.evictCardsCache(userId);
        var savedCard = cardsService.saveOrUpdate(cardsService.createEntityFromDto(card, userId));
        return cardsService.createDtoFromEntity(savedCard, userId);
    }

    @KafkaSparuralMapping("cards/attach-confirm")
    public UserCardDto confirmAttach(@RequestParam Long userId,
                                     @Payload CodeDto codeDto) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var userCard = loymaxService.attachConfirm(loymaxUser, codeDto);

        var entity = cardsService.createEntityFromDto(userCard, userId);
        var card = cardsService.saveOrUpdate(entity);
        return cardsService.createDtoFromEntity(card, userId);
    }

    @KafkaSparuralMapping("cards/attach-send-confirm-code")
    public Boolean attachSendConfirmCode(@RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.attachSendConfirmCode(loymaxUser);
        return true;
    }

    @KafkaSparuralMapping("cards/change-block-state")
    public UserCardDto changeBlockState(@Payload CardPasswordDto cardPasswordDto,
                                        @RequestParam Long userId,
                                        @RequestParam Long id) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var loymaxCard = loymaxCardService.findByLocalCardId(id);

        UserCardDto userCard = loymaxService.changeCardBlockState(
                loymaxUser, cardPasswordDto, loymaxCard.getLoymaxCardId());

        var entity = cardsService.createEntityFromDto(userCard, userId);
        var savedEntity = cardsService.saveOrUpdate(entity);
        cardsService.bind(savedEntity.getId(), userId, true, true);
        loymaxCardService.bindCard(savedEntity.getId(), loymaxCard.getLoymaxCardId());
        return cardsService.createDtoFromEntity(entity, userId);
    }

    @KafkaSparuralMapping("cards/replace")
    public UserCardDto replace(@RequestParam Long userId,
                               @RequestParam Long id,
                               @Payload CardNumberPasswordRequestDto cardReplaceRequestDto) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var loymaxCardRequest = new LoymaxUserCardRequestDto();
        loymaxCardRequest.setCardNumber(cardReplaceRequestDto.getNumber());
        loymaxCardRequest.setPassword(cardReplaceRequestDto.getPassword());

        var loymaxCard = loymaxCardService.findByLocalCardId(id);
        var userCard = loymaxService
                .replaceUserCard(loymaxUser, loymaxCardRequest, loymaxCard.getLoymaxCardId());
        log.info("1." + userCard.toString());
        var entity = cardsService.createEntityFromDto(userCard, userId);
        entity.setId(null);
        var saveEntity = cardsService.saveOrUpdate(entity);
        log.info("2." + saveEntity.toString());
        cardsService.bind(saveEntity.getId(), userId, true, true);
        loymaxCardService.bindCard(saveEntity.getId(), userCard.getId());
        return cardsService.createDtoFromEntity(entity, userId);
    }

    @KafkaSparuralMapping("cards/select")
    public UserCardsAccountsDto select(@RequestParam Long userId) throws ExecutionException, InterruptedException {
        return cardsService.selectCardsAndAccounts(userId);
    }

    @KafkaSparuralMapping("cards/qr")
    public CardQrDto getQrCode(@RequestParam Long id,
                               @RequestParam Long userId) {
        var entity = cardQrService.fetchQr(id, userId);
        var dto = new CardQrDto();
        dto.setCardId(entity.getCardId());
        dto.setCode(entity.getCode());
        dto.setId(entity.getId());
        dto.setCodeGeneratedDate(entity.getCodeGeneratedDate());
        dto.setLifeTime(entity.getLifeTime());
        return dto;
    }
}