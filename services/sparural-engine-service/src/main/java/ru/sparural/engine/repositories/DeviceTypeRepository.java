package ru.sparural.engine.repositories;

import java.util.Optional;

public interface DeviceTypeRepository {
    Optional<String> findNameTypesById(Long id);
}
