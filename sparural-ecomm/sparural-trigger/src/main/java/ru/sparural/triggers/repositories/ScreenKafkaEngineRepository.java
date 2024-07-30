package ru.sparural.triggers.repositories;

import ru.sparural.engine.api.dto.ScreenDto;

import java.util.concurrent.CompletableFuture;

/**
 * @author Vorobyev Vyacheslav
 */
public interface ScreenKafkaEngineRepository {
    CompletableFuture<ScreenDto> getById(Long screenId);
}
