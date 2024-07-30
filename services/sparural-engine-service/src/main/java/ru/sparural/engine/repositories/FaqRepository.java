package ru.sparural.engine.repositories;


import ru.sparural.engine.entity.FaqEntity;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface FaqRepository {
    List<FaqEntity> fetch(int offset, int limit);

    Optional<FaqEntity> get(Long id) throws ResourceNotFoundException;

    Optional<FaqEntity> update(Long id, FaqEntity faqEntity) throws ResourceNotFoundException;

    Boolean delete(Long id) throws ResourceNotFoundException;

    Optional<FaqEntity> create(FaqEntity faqEntity);
}