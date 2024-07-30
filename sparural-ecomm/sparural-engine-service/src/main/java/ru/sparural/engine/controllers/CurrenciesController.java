package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.services.CurrencyService;
import ru.sparural.engine.utils.mappers.CurrencyMapper;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class CurrenciesController {
    private final CurrencyService currencyService;
    private final CurrencyMapper mapper = CurrencyMapper.INSTANCE;

    @KafkaSparuralMapping("currencies/index")
    public List<Currency> list(@RequestParam Integer offset,
                               @RequestParam Integer limit) {
        var result = currencyService.fetchAll(offset, limit);
        return result.stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }
}
