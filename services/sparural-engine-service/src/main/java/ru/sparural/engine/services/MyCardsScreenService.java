package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.screen.mycards.MyCardsInfoScreenDto;
import ru.sparural.engine.api.dto.screen.mycards.Status;
import ru.sparural.engine.entity.Card;
import ru.sparural.engine.entity.LoymaxUser;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MyCardsScreenService {
    MyCardsInfoScreenDto getMyCardsScreenV2(Long userId) throws ExecutionException, InterruptedException;

    MyCardsInfoScreenDto getMyCardsScreenV1(Long userId) throws ExecutionException, InterruptedException;

    List<Card> selectAndBindUserCards(LoymaxUser loymaxUser, Long userId);

    Status selectAndBindUserStatus(LoymaxUser loymaxUser);
}
