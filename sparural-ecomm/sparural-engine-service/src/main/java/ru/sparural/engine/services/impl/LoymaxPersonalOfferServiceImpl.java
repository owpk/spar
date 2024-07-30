package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxPersonalOffersEntity;
import ru.sparural.engine.repositories.LoymaxPersonalOfferRepository;
import ru.sparural.engine.services.LoymaxPersonalOfferService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class LoymaxPersonalOfferServiceImpl implements LoymaxPersonalOfferService {
    private final LoymaxPersonalOfferRepository repository;

    @Override
    public void bindLoymaxOfferToPersonalOffer(Long loymaxOfferId, Long personalOfferId) {
        var entity = new LoymaxPersonalOffersEntity();
        entity.setPersonalOfferId(personalOfferId);
        entity.setLoymaxOfferId(loymaxOfferId);
        repository.saveOrUpdate(entity);
    }

    @Override
    public void findByLoymaxOfferId(String loymaxOfferId) throws ResourceNotFoundException {
        repository.findByLoymaxOfferId(Long.valueOf(loymaxOfferId))
                .orElseThrow(() -> new ResourceNotFoundException("Loymax offer with id: " + loymaxOfferId + " not found"));
    }
}

