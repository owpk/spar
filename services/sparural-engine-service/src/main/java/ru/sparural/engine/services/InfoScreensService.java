package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.InfoScreenDto;
import ru.sparural.engine.entity.InfoScreen;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;

import java.util.List;

public interface InfoScreensService {


    InfoScreenDto create(InfoScreenDto data) throws ValidationException, ResourceNotFoundException;

    InfoScreenDto update(Long id, InfoScreenDto data) throws ValidationException, ResourceNotFoundException;

    List<InfoScreenDto> list(int offset, int limit, Long city, Boolean showOnlyPublic, Long dateStart, Long dateEnd);

    InfoScreenDto get(Long id) throws ResourceNotFoundException;

    boolean delete(Long id);

    InfoScreenDto createDto(InfoScreen infoScreen);

}
