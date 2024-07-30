package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.cards.CardQrDto;
import ru.sparural.engine.entity.CardQr;


public interface CardQrService {
    CardQrDto save(Long cardId, CardQrDto cardQrDto);

    CardQrDto update(Long cardId, CardQrDto cardQrDto);

    CardQr createEntityFromDto(CardQrDto cardQrDto);

    CardQrDto createDtoFromEntity(CardQr cardQr);

    boolean checkIfCardExists(Long id);

    CardQrDto get(Long id);

}