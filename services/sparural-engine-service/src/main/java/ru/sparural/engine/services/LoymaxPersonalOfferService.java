package ru.sparural.engine.services;

public interface LoymaxPersonalOfferService {
    void bindLoymaxOfferToPersonalOffer(Long loymaxOfferId, Long personalOfferId);

    void findByLoymaxOfferId(String loymaxOfferId);

}

