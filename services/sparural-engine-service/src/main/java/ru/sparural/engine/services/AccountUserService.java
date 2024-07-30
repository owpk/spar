package ru.sparural.engine.services;

import java.util.List;

public interface AccountUserService {
    void save(Long userId, Long accountId);

    void batchSaveAsync(Long userId, List<Long> ids);
}
