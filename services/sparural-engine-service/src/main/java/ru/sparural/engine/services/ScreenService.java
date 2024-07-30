package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.engine.entity.Screen;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface ScreenService {

    List<Screen> fetch(Long offset, Long limit);

    ScreenDto createDtoFromEntity(Screen entity);

    String findCodeById(Long screenId);

    Long findIdByCode(String screen);

    Screen findById(Long id);
}
