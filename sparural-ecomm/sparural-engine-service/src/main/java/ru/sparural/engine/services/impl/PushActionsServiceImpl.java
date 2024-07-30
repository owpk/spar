package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.engine.entity.Screen;
import ru.sparural.engine.repositories.PushActionsRepository;
import ru.sparural.engine.services.PushActionsService;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class PushActionsServiceImpl implements PushActionsService {

    private final DtoMapperUtils dtoMapperUtils;
    private final PushActionsRepository pushActionsRepository;

    @Override
    public List<ScreenDto> list(Integer offset, Integer limit) {
        return createDTOListFromEntityList(pushActionsRepository.list(offset, limit));
    }

    @Override
    public List<ScreenDto> createDTOListFromEntityList(List<Screen> screenList) {
        return dtoMapperUtils.convertList(ScreenDto.class, screenList);
    }
}
