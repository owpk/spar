package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.check.Reward;

import java.util.List;

public interface RewardService {
    Reward saveOrUpdate(Reward dto);

    ru.sparural.engine.entity.Reward createEntityFromDto(Reward dto);

    Reward createDtoFromEntity(ru.sparural.engine.entity.Reward entity);

    List<Reward> getListByCheckId(Long checkId);

    List<Reward> createDtoList(List<ru.sparural.engine.entity.Reward> entityList);

    List<ru.sparural.engine.entity.Reward> batchSave(List<ru.sparural.engine.entity.Reward> list);
}
