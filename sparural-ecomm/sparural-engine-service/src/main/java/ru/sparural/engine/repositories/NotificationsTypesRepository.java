package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.NotificationsType;

import java.util.List;
import java.util.Optional;

public interface NotificationsTypesRepository {
    List<NotificationsType> list(Integer offset, Integer limit);

    Optional<NotificationsType> get(Long id);
}
