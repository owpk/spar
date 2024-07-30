package ru.sparural.triggers.services;

import ru.sparural.triggerapi.dto.TriggersTypeDTO;
import ru.sparural.triggers.entities.TriggersType;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface TriggersTypeService {
    List<TriggersTypeDTO> list(int offset, int limit);

    List<TriggersTypeDTO> createDTOListFromEntityList(List<TriggersType> triggersTypes);

    List<TriggersTypeDTO> getAll();

    TriggersTypeDTO get(Long id);
}
