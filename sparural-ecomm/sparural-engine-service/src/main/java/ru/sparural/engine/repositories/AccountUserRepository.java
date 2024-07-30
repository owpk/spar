package ru.sparural.engine.repositories;

import java.util.List;

public interface AccountUserRepository {
    void save(Long userId, Long accountId);

    void batchBind(Long userId, List<Long> ids);
}
