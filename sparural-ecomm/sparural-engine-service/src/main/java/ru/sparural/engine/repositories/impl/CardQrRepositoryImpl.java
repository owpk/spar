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
    public Optional<CardQr> getByCardId(Long id) throws ResourceNotFoundException {
        return dslContext.selectFrom(CardsQr.CARDS_QR)
                .where(CardsQr.CARDS_QR.CARD_ID.eq(id))
                .fetchOptionalInto(CardQr.class);
    }

    @Override
    public Optional<CardQr> update(Long cardId, CardQr cardQr) {
        return dslContext.update(CardsQr.CARDS_QR)
                .set(CardsQr.CARDS_QR.CODE, cardQr.getCode())
                .set(CardsQr.CARDS_QR.CODE_GENERATED_DATE, cardQr.getCodeGeneratedDate())
                .set(CardsQr.CARDS_QR.LIFE_TIME, cardQr.getLifeTime().intValue())
                .set(CardsQr.CARDS_QR.UPDATED_AT, TimeHelper.currentTime())
                .where(CardsQr.CARDS_QR.CARD_ID.eq(cardId))
                .returning()
                .fetchOptionalInto(CardQr.class);
    }

    @Override
    public Optional<CardQr> saveOrUpdate(Long cardId, CardQr cardQr) {
        return dslContext.insertInto(CardsQr.CARDS_QR)
                .set(CardsQr.CARDS_QR.CODE, cardQr.getCode())
                .set(CardsQr.CARDS_QR.CODE_GENERATED_DATE, cardQr.getCodeGeneratedDate())
                .set(CardsQr.CARDS_QR.LIFE_TIME, cardQr.getLifeTime().intValue())
                .set(CardsQr.CARDS_QR.CARD_ID, cardId)
                .set(CardsQr.CARDS_QR.CREATED_AT, TimeHelper.currentTime())
                .onConflict(CardsQr.CARDS_QR.CARD_ID)
                .doUpdate()
                .set(CardsQr.CARDS_QR.CODE, cardQr.getCode())
                .set(CardsQr.CARDS_QR.CODE_GENERATED_DATE, cardQr.getCodeGeneratedDate())
                .set(CardsQr.CARDS_QR.LIFE_TIME, cardQr.getLifeTime().intValue())
                .set(CardsQr.CARDS_QR.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(CardQr.class);
    }
}