package ru.sparural.engine.services;

import ru.sparural.engine.entity.MinVersionAppEntity;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MinVersionAppService {
    List<MinVersionAppEntity> getAll();

    MinVersionAppEntity getLast();

    MinVersionAppEntity create(MinVersionAppEntity minVersionAppEntity);
}
