package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.ClientStatusEntity;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface StatusRepository {
    Optional<ClientStatusEntity> saveOrUpdate(ClientStatusEntity clientStatus);
}
