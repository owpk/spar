package ru.sparural.engine.repositories;

import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.entity.CheckDBEntity;
import ru.sparural.engine.entity.CheckEntity;
import ru.sparural.engine.entity.LoymaxUser;

import java.util.List;
import java.util.Optional;

public interface CheckRepository {
    Optional<CheckEntity> get(Long id, Long cardId);

    Optional<CheckEntity> save(CheckEntity entity);

    void saveLoymaxChecks(Long checkId, String historyId);

    List<LoymaxUser> getAllUserId();

    Optional<Long> getMerchantIdOfLastCheck(Long userId);

    Optional<CheckDBEntity> getById(Long id);

    Optional<Long> getCheckIdByLoymaxId(String id);

    List<CheckEntity> getLastCheck(UserFilterDto filter, Long startTime);

    void saveIsNotifCheck(List<Long> checkIds);

    List<CheckEntity> getAllChecksByUserId(Long x);

    List<CheckEntity> batchSaveOrUpdate(List<CheckEntity> checkEntities);
}
