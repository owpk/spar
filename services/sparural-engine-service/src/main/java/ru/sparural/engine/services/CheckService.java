package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.entity.CheckDBEntity;
import ru.sparural.engine.entity.CheckEntity;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheckItem;

import java.util.List;

public interface CheckService {

    CheckDto get(Long id, Long userId);

    CheckDto createDto(CheckEntity entity);

    CheckEntity createEntity(CheckDto dto);

    Long findCardIdByUserid(Long userId);

    List<CheckEntity> loadChecksForUser(Long userId);

    CheckDto save(CheckDto dto);

    void saveLoymaxChecks(Long checkId, String historyId);

    List<LoymaxUser> getAllUserId();

    Long getMerchantIdOfLastCheck(Long userId);

    CheckDBEntity getByLoymaxId(String id);

    List<CheckEntity> getLastCheck(UserFilterDto filter, Long startTime);

    void saveIsNotifCheck(List<Long> checkIds);

    List<CheckEntity> getAllChecksByUserId(Long x);

    List<CheckDto> processingSaveOrUpdateChecks(List<LoymaxCheckItem> loymaxChecks, Long userId);

    List<CheckEntity> batchSaveOrUpdate(List<CheckEntity> checkEntityList);
}
