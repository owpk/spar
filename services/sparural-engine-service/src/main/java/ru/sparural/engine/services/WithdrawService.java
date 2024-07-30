package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.check.Withdraw;

import java.util.List;

public interface WithdrawService {
    Withdraw saveOrUpdate(Withdraw dto);

    ru.sparural.engine.entity.Withdraw createEntityFromDto(Withdraw dto);

    Withdraw createDtoFromEntity(ru.sparural.engine.entity.Withdraw entity);

    List<Withdraw> getListByCheckId(Long checkId);

    List<Withdraw> createDtoList(List<ru.sparural.engine.entity.Withdraw> entityList);

    List<ru.sparural.engine.entity.Withdraw> batchSave(List<ru.sparural.engine.entity.Withdraw> list);
}
