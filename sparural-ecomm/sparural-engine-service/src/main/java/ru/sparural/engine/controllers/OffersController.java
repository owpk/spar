package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.main.OfferDto;
import ru.sparural.engine.providers.OffersProvider;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class OffersController {
    private final OffersProvider offersProvider;

    @KafkaSparuralMapping("offers/index")
    public List<OfferDto> list(@RequestParam Integer offset,
                               @RequestParam Integer limit,
                               @RequestParam Long userId) throws ResourceNotFoundException {
        return offersProvider.getOfferList(offset, limit, userId);
    }
}
