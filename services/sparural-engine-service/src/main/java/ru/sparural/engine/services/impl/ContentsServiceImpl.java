package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Content;
import ru.sparural.engine.repositories.ContentsRepository;
import ru.sparural.engine.services.ContentsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentsServiceImpl implements ContentsService<Content> {

    private final ContentsRepository contentsRepository;

    public List<Content> list(int offset, int limit) {
        return contentsRepository.getList(offset, limit);
    }

    public Content create(Content data) {
        return contentsRepository.create(data)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create content, alias exists"));
    }

    @Override
    public Content update(String alias, Content data) {
        return contentsRepository.updateByAlias(alias, data);
    }

    @Override
    public boolean delete(String alias) {
        return contentsRepository.deleteByAlias(alias);
    }

    @Override
    public Content get(String alias) {
        return contentsRepository.getByAlias(alias);
    }
}

