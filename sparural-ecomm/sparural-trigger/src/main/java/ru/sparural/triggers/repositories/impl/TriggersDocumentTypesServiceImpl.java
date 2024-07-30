package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.triggers.exceptions.ResourceNotFoundException;
import ru.sparural.triggers.repositories.TriggersDocumentTypesRepository;

@Service
@RequiredArgsConstructor
public class TriggersDocumentTypesServiceImpl implements ru.sparural.triggers.repositories.TriggersDocumentTypesService {
    private final TriggersDocumentTypesRepository repository;

    @Override
    public Long findIdByName(String name) {
        return repository.findIdByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("This type of trigger document not found"));
    }
}
