package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.sparural.engine.api.dto.OffersCounterDto;
import ru.sparural.engine.services.OffersCounterService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.tables.pojos.OffersCounters;

import java.util.List;
import java.util.stream.Collectors;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class OffersCountersController {

    private final OffersCounterService offersCounterService;
    private final ModelMapper modelMapper;

    @KafkaSparuralMapping("offers-counters/index")
    public List<OffersCounterDto> list(@RequestParam Integer offset,
                                       @RequestParam Integer limit) {
        var result = offersCounterService.index(offset, limit);
        return result.stream()
                .map(attr -> modelMapper.map(attr, OffersCounterDto.class))
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("offers-counters/get")
    public OffersCounterDto get(@RequestParam Long id) {
        var result = offersCounterService.get(id);
        return modelMapper.map(result, OffersCounterDto.class);
    }

    @KafkaSparuralMapping("offers-counters/create")
    public OffersCounterDto create(@Payload OffersCounterDto data) {
        var result = offersCounterService.create(modelMapper.map(data, OffersCounters.class));
        return modelMapper.map(result, OffersCounterDto.class);
    }

    @KafkaSparuralMapping("offers-counters/update")
    public OffersCounterDto update(@Payload OffersCounterDto updateDto, @RequestParam Long id) {
        var result = offersCounterService.update(id, modelMapper.map(updateDto, OffersCounters.class));
        return modelMapper.map(result, OffersCounterDto.class);
    }

    @KafkaSparuralMapping("offers-counters/delete")
    public Boolean delete(@RequestParam Long id) {
        return offersCounterService.delete(id);
    }

    @KafkaSparuralMapping("offers-counters/import")
    public void importCounterForUser(@RequestParam Long loymaxUserId,
                                     @RequestParam Integer loymaxCounterId,
                                     @RequestParam Long counterId) {
        offersCounterService.importAndBindForUser(loymaxUserId, loymaxCounterId, counterId);
    }

}
