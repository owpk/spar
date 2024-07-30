package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.ClientStatusEntity;

import java.util.List;
import java.util.Optional;

public interface ClientStatusRepository {
    List<ClientStatusEntity> fetch(int offset, int limit);

    Optional<ClientStatusEntity> get(Long id);
}
