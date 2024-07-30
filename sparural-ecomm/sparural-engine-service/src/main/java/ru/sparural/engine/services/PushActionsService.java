package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.engine.entity.Screen;

import java.util.List;

public interface PushActionsService {
    List<ScreenDto> list(Integer offset, Integer limit);

    List<ScreenDto> createDTOListFromEntityList(List<Screen> screenList);
}
