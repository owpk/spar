package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.entity.AccountsLifeTimesByTime;

import java.util.List;

public interface AccountsLifeTimesByTimeService {

    List<AccountsLifeTimesByTime> list(int offset, int limit, long id, long userId);

    Boolean deleteByAccountId(Long accountId);

    AccountsLifeTimesByTimeDTO save(AccountsLifeTimesByTimeDTO dto);

    AccountsLifeTimesByTime createEntityFromDTO(AccountsLifeTimesByTimeDTO dto);

    AccountsLifeTimesByTimeDTO createDTOFromEntity(AccountsLifeTimesByTime entity);


    List<AccountsLifeTimesByTime> fetchByAccId(List<Long> collect);

    void deleteByIds(List<Long> ltbtToDelete);

    void batchSave(List<AccountsLifeTimesByTime> values);
}
