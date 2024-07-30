package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Reward;

import java.util.List;
import java.util.Optional;

public interface RewardRepository {
    Optional<Reward> saveOrUpdate(Reward entity);

    List<Reward> getListByCheckId(Long checkId);

    List<Reward> batchSave(List<Reward> list);
}
