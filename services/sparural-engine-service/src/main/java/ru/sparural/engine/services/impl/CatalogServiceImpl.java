package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CatalogDto;
import ru.sparural.engine.entity.Catalog;
import ru.sparural.engine.repositories.CatalogRepository;
import ru.sparural.engine.services.CatalogsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogsService {

    private final CatalogRepository catalogRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public CatalogDto createDtoFromEntity(Catalog catalog) {
        return dtoMapperUtils.convert(catalog, CatalogDto.class);
    }

    @Override
    public Catalog createEntityFromDto(CatalogDto catalog) {
        return dtoMapperUtils.convert(catalog, Catalog.class);
    }

    @Override
    public Catalog create(CatalogDto data) {
        var entity = createEntityFromDto(data);
        return catalogRepository.create(entity)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create catalog"));
    }

    @Override
    public Catalog update(Long id, CatalogDto data) {
        return catalogRepository.update(id, createEntityFromDto(data))
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update catalog"));
    }

    @Override
    public Catalog get(Long id) {
        return catalogRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog not found"));
    }

    @Override
    public boolean delete(Long id) {
        return catalogRepository.delete(id);
    }

    @Override
    public List<Catalog> list(int offset, int limit, Long city) {
        if (city == null || city == 0)
            return catalogRepository.fetch(offset, limit);
        return catalogRepository.findByCity(offset, limit, city);
    }

}