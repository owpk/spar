package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Catalog;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository {

    Optional<Catalog> create(Catalog data);

    boolean delete(Long id);

    Optional<Catalog> update(Long id, Catalog data);

    Optional<Catalog> get(Long id);

    List<Catalog> fetch(int offset, int limit);

    List<Catalog> findByCity(int offset, int limit, Long city);
}
