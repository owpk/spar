package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.CatalogDto;
import ru.sparural.engine.entity.Catalog;

import java.util.List;

public interface CatalogsService {

    Catalog create(CatalogDto data);

    Catalog update(Long id, CatalogDto data);

    Catalog get(Long id);

    boolean delete(Long id);

    List<Catalog> list(int offset, int limit, Long city);

    CatalogDto createDtoFromEntity(Catalog catalog);

    Catalog createEntityFromDto(CatalogDto catalog);
}
