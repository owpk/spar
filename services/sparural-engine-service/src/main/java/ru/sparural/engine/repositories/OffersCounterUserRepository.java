package ru.sparural.engine.repositories;

import ru.sparural.tables.pojos.OffersCounterUser;

/**
 * @author Vorobyev Vyacheslav
 */
public interface OffersCounterUserRepository {
    void saveOrUpdate(OffersCounterUser data);
}
