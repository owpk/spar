package ru.sparural.engine.services;

import ru.sparural.tables.pojos.Counters;

import java.util.List;

public interface CounterService {
    List<Counters> index(Integer offset, Integer limit);

    Counters get(Long id);

    Counters create(Counters data);

    Counters update(Long id, Counters data);

    Boolean delete(Long id);

}
