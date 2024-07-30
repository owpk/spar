package ru.sparural.engine.services;

/**
 * @author Vorobyev Vyacheslav
 */
public interface OffersCounterUserService {
    void bindOfferToUser(Integer value, Long userId, Long counterOfferId);
}
