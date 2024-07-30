package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.ExternalDocument;
import ru.sparural.engine.repositories.ExternalDocumentsRepository;
import ru.sparural.engine.services.ExternalDocumentsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class ExternalDocumentsServiceImpl implements ExternalDocumentsService<ExternalDocument> {

    private final ExternalDocumentsRepository externalDocumentsRepository;

    @Override
    public List<ExternalDocument> list(int offset, int limit) {
        return externalDocumentsRepository.getList(offset, limit);
    }

    @Override
    public ExternalDocument get(String alias) throws ResourceNotFoundException {
        return externalDocumentsRepository.getByAlias(alias).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public ExternalDocument create(ExternalDocument data) {
        return externalDocumentsRepository.create(data)
                .orElseThrow(() -> new ResourceNotFoundException("Document not created, alias exists"));
    }

    @Override
    public ExternalDocument update(String alias, ExternalDocument data) {
        return externalDocumentsRepository.updateByAlias(alias, data)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public boolean delete(String alias) {
        return externalDocumentsRepository.deleteByAlias(alias);
    }


}
