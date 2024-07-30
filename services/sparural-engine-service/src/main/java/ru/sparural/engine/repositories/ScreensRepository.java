package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Screen;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface ScreensRepository {
    List<Screen> fetch(Long offset, Long limit);

    Optional<Screen> get(Long id);

    Optional<String> findCodeById(Long screenId);

    Optional<Long> findIdByCode(String screen);
}
