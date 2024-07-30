package ru.sparural.engine.repositories;

import ru.sparural.tables.pojos.Counters;

import java.util.List;
import java.util.Optional;

public interface CountersRepository {
    Optional<Counters> saveOrUpdate(Counters counters);

    List<Counters> list(Integer offset, Integer limit);

    Optional<Counters> fetchById(Long id);

    Optional<Counters> update(Long id, Counters data);

    Boolean delete(Long id);
}
