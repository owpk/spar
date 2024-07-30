package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.cards.CardQrDto;
import ru.sparural.engine.entity.CardQr;
import ru.sparural.engine.repositories.CardQrRepository;
import ru.sparural.engine.services.CardQrService;
import ru.sparural.engine.utils.DtoMapperUtils;


@Service
@RequiredArgsConstructor
public class CardQrServiceImpl implements CardQrService {

    private final DtoMapperUtils dtoMapperUtils;
    private final CardQrRepository cardQrRepository;

    @Override
    public CardQrDto save(Long cardId, CardQrDto cardQrDto) {
        return createDtoFromEntity(cardQrRepository.save(cardId, createEntityFromDto(cardQrDto)).get());
    }

    @Override
    public CardQrDto update(Long cardId, CardQrDto cardQrDto) {

        return createDtoFromEntity(cardQrRepository.update(cardId, createEntityFromDto(cardQrDto)).get());
    }

    @Override
    public CardQr createEntityFromDto(CardQrDto cardQrDto) {
        return dtoMapperUtils.convert(cardQrDto, CardQr.class);
    }

    @Override
    public CardQrDto createDtoFromEntity(CardQr cardQr) {
        return dtoMapperUtils.convert(cardQr, CardQrDto.class);
    }

    @Override
    public boolean checkIfCardExists(Long id) {
        return cardQrRepository.get(id).isPresent();
    }

    @Override
    public CardQrDto get(Long id) {
        return createDtoFromEntity(cardQrRepository.get(id).get());
    }


}