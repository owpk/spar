package ru.sparural.engine.repositories.impl;


import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.CardQr;
import ru.sparural.engine.repositories.CardQrRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.CardsQr;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardQrRepositoryImpl implements CardQrRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<CardQr> get(Long id) throws ResourceNotFoundException {
        return dslContext.selectFrom(CardsQr.CARDS_QR)
                .where(CardsQr.CARDS_QR.CARDID.eq(id))
                .fetchOptionalInto(CardQr.class);
    }

    @Override
    public Optional<CardQr> update(Long cardId, CardQr cardQr) throws ResourceNotFoundException {
        return dslContext.update(CardsQr.CARDS_QR)
                .set(CardsQr.CARDS_QR.CODE, cardQr.getCode())
                .set(CardsQr.CARDS_QR.CODEGENERATEDDATE, cardQr.getCodeGeneratedDate())
                .set(CardsQr.CARDS_QR.LIFETIME, cardQr.getLifeTime())
                .set(CardsQr.CARDS_QR.UPDATEDAT, TimeHelper.currentTime())
                .where(CardsQr.CARDS_QR.CARDID.eq(cardId))
                .returning()
                .fetchOptionalInto(CardQr.class);
    }

    @Override
    public Optional<CardQr> save(Long cardId, CardQr cardQr) {
        return dslContext.insertInto(CardsQr.CARDS_QR)
                .set(CardsQr.CARDS_QR.CODE, cardQr.getCode())
                .set(CardsQr.CARDS_QR.CODEGENERATEDDATE, cardQr.getCodeGeneratedDate())
                .set(CardsQr.CARDS_QR.LIFETIME, cardQr.getLifeTime())
                .set(CardsQr.CARDS_QR.CARDID, cardId)
                .set(CardsQr.CARDS_QR.CREATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(CardQr.class);
    }
}