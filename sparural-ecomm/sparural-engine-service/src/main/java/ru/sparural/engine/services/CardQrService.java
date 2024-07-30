package ru.sparural.engine.services;

import ru.sparural.engine.entity.CardQr;


public interface CardQrService {
    CardQr saveOrUpdate(Long cardId, CardQr cardQrDto);

    CardQr update(Long cardId, CardQr cardQrDto);

    boolean checkIfRecordExistsByCardId(Long id);

    CardQr get(Long id);

    CardQr fetchQr(Long cardId, Long userId);
}