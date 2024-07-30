package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.engine.entity.Screen;
import ru.sparural.engine.repositories.ScreensRepository;
import ru.sparural.engine.services.ScreenService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService {

    private final ScreensRepository screensRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<Screen> fetch(Long offset, Long limit) {
        return screensRepository.fetch(offset, limit);
    }

    @Override
    public ScreenDto createDtoFromEntity(Screen entity) {
        return dtoMapperUtils.convert(entity, ScreenDto.class);
    }

    @Override
    public String findCodeById(Long screenId) {
        return screensRepository.findCodeById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen with this id not found"));
    }

    @Override
    public Long findIdByCode(String screen) {
        return screensRepository.findIdByCode(screen)
                .orElseThrow(() -> new ResourceNotFoundException("Screen with this code not found"));
    }

    @Override
    public Screen findById(Long id) {
        return screensRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen with id: " + id + " not found"));
    }
}