package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Screen;

import java.util.List;

public interface PushActionsRepository {
    List<Screen> list(Integer offset, Integer limit);
}
