package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.ExternalDocument;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ExternalDocumentsRepository {

    Optional<ExternalDocument> create(ExternalDocument data);

    boolean deleteByAlias(String alias);

    Optional<ExternalDocument> updateByAlias(String alias, ExternalDocument data);

    Optional<ExternalDocument> getByAlias(String alias) throws ResourceNotFoundException;

    List<ExternalDocument> getList(int offset, int limit);
}
