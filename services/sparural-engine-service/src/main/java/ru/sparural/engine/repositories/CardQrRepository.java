package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.CardQr;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.Optional;

public interface CardQrRepository {
    Optional<CardQr> get(Long id) throws ResourceNotFoundException;

    Optional<CardQr> update(Long cardId, CardQr cardQr) throws ResourceNotFoundException;

    Optional<CardQr> save(Long cardId, CardQr cardQr);
}