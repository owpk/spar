package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.repositories.OffersCounterUserRepository;
import ru.sparural.engine.services.OffersCounterUserService;
import ru.sparural.tables.pojos.OffersCounterUser;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class OffersCounterUserServiceImpl implements OffersCounterUserService {
    private final OffersCounterUserRepository offersCounterUserRepository;

    @Override
    public void bindOfferToUser(Integer value, Long userId, Long counterOfferId) {
        var pojo = new OffersCounterUser();
        pojo.setOfferId(counterOfferId);
        pojo.setUserId(userId);
        offersCounterUserRepository.saveOrUpdate(pojo);
    }
}