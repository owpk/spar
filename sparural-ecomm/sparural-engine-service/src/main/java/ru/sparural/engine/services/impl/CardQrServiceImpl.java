package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.CardQr;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.repositories.CardQrRepository;
import ru.sparural.engine.services.CardQrService;
import ru.sparural.engine.services.LoymaxCardService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class CardQrServiceImpl implements CardQrService {

    private final CardQrRepository cardQrRepository;
    private final LoymaxService loymaxService;
    private final LoymaxCardService loymaxCardService;

    @Override
    public CardQr saveOrUpdate(Long cardId, CardQr entity) {
        return cardQrRepository.saveOrUpdate(cardId, entity)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot save card qr with card id: " + cardId));
    }

    @Override
    public CardQr update(Long cardId, CardQr entity) {
        return cardQrRepository.update(cardId, entity)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update card qr with card id: " + cardId));
    }

    @Override
    public boolean checkIfRecordExistsByCardId(Long id) {
        return cardQrRepository.getByCardId(id).isPresent();
    }

    @Override
    public CardQr get(Long id) {
        return cardQrRepository.getByCardId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card qr not found with id: " + id));
    }

    /**
     * We check if there is a qr card in the database,
     * if not, we get it and add it to the database.
     * If the card is in the database, you should request the generation of a new code
     * if there are 10 minutes left before the expiration of the code.
     * (Card lifetime is 85800 seconds )
     */
    @Override
    @Transactional
    public CardQr fetchQr(Long cardId, Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        if (!checkIfRecordExistsByCardId(cardId)) {
            var loymaxCard = loymaxCardService.findByLocalCardId(cardId);
            var cardQr = loymaxService.getQrCode(loymaxUser, loymaxCard.getLoymaxCardId());
            return saveOrUpdate(cardId, cardQr);
        }

        var cardQrEntity = get(cardId);

        if (Instant.now().getEpochSecond() - cardQrEntity.getCodeGeneratedDate() > cardQrEntity.getLifeTime()) {
            var loymaxCard = loymaxCardService.findByLocalCardId(cardId);
            var cardQrFromLoymax = loymaxService.getQrCode(loymaxUser, loymaxCard.getLoymaxCardId());
            return update(cardId, cardQrFromLoymax);
        }

        cardQrEntity.setLifeTime(cardQrEntity.getLifeTime() - (Instant.now().getEpochSecond() - cardQrEntity.getCodeGeneratedDate()));
        return update(cardId, cardQrEntity);
    }

}