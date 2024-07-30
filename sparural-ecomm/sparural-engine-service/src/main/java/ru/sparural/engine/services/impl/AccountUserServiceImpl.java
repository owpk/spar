package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.repositories.AccountUserRepository;
import ru.sparural.engine.services.AccountUserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountUserServiceImpl implements AccountUserService {

    private final AccountUserRepository accountUserRepository;

    @Override
    public void save(Long userId, Long accountId) {
        accountUserRepository.save(userId, accountId);
    }

    @Override
    public void batchSaveAsync(Long userId, List<Long> ids) {
        accountUserRepository.batchBind(userId, ids);
    }
}
