package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.InfoScreen;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;

import java.util.List;

public interface InfoScreensRepository {

    InfoScreen create(InfoScreen data) throws ValidationException, ResourceNotFoundException;

    boolean delete(Long id);

    InfoScreen update(Long id, InfoScreen data) throws ValidationException, ResourceNotFoundException;

    InfoScreen get(Long id) throws ResourceNotFoundException;

    List<InfoScreen> list(int offset, int limit, Boolean showOnlyPublic, Long dateStart, Long dateEnd);

    List<InfoScreen> list(int offset, int limit, Long city, Boolean showOnlyPublic, Long dateStart, Long dateEnd);
}

