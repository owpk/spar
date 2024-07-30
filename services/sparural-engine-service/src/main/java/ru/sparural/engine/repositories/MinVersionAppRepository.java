package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MinVersionAppEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MinVersionAppRepository {
    List<MinVersionAppEntity> getALl();

    Optional<MinVersionAppEntity> getLast();

    Optional<MinVersionAppEntity> create(MinVersionAppEntity minVersionAppEntity);
}
