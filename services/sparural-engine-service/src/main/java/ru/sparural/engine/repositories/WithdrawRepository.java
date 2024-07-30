package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Withdraw;

import java.util.List;
import java.util.Optional;

public interface WithdrawRepository {
    Optional<Withdraw> saveOrUpdate(Withdraw entity);

    List<Withdraw> getListByCheckId(Long checkId);

    List<Withdraw> batchSave(List<Withdraw> list);
}
