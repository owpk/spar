package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.entity.CheckEntity;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheckItem;
import ru.sparural.tables.pojos.Checks;

import java.util.List;
import java.util.Optional;

public interface CheckService {

    CheckDto get(Long id, Long userId);

    CheckDto createDto(CheckEntity entity);

    CheckEntity createEntity(CheckDto dto);

    List<CheckEntity> loadChecksForUser(Long userId);

    CheckDto saveOrUpdate(CheckDto dto);

    Long getMerchantIdOfLastCheck(Long userId);

    Optional<Checks> getByLoymaxId(String id);

    List<CheckEntity> getLastCheck(UserFilterDto filter, Long startTime);

    void saveIsNotifCheck(List<Long> checkIds);

    List<CheckEntity> getAllChecksByUserId(Long userId);

    List<CheckEntity> processingSaveOrUpdateChecks(List<LoymaxCheckItem> loymaxChecksList, Long userId);
}
