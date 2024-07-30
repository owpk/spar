package ru.sparural.triggers.services;

import ru.sparural.engine.api.dto.ScreenDto;

import java.util.concurrent.CompletableFuture;

/**
 * @author Vorobyev Vyacheslav
 */
public interface ScreenKafkaEngineService {
    ScreenDto getById(Long screenId);

    CompletableFuture<ScreenDto> getAsync(Long screenId);
}
