package ru.sparural.triggers.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.triggers.repositories.ScreenKafkaEngineRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class ScreenKafkaEngineServiceImpl implements ru.sparural.triggers.services.ScreenKafkaEngineService {
    private final ScreenKafkaEngineRepository screenKafkaEngineRepository;

    @Override
    public ScreenDto getById(Long screenId) {
        try {
            return screenKafkaEngineRepository.getById(screenId).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<ScreenDto> getAsync(Long screenId) {
        return screenKafkaEngineRepository.getById(screenId);
    }
}
