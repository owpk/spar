package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.sparural.engine.api.dto.counters.CounterDto;
import ru.sparural.engine.services.CounterService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.tables.pojos.Counters;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class CountersController {
    private final ModelMapper modelMapper;
    private final CounterService counterService;

    @KafkaSparuralMapping("counters/index")
    public List<CounterDto> list(@RequestParam Integer offset,
                                 @RequestParam Integer limit) {
        var result = counterService.index(offset, limit);
        return result.stream()
                .map(attr -> modelMapper.map(attr, CounterDto.class))
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("counters/get")
    public CounterDto get(@RequestParam Long id) {
        var result = counterService.get(id);
        return modelMapper.map(result, CounterDto.class);
    }

    @KafkaSparuralMapping("counters/create")
    public CounterDto create(@Payload CounterDto data) {
        var result = counterService.create(modelMapper.map(data, Counters.class));
        return modelMapper.map(result, CounterDto.class);
    }

    @KafkaSparuralMapping("counters/update")
    public CounterDto update(@Payload CounterDto updateDto, @RequestParam Long id) {
        var result = counterService.update(id, modelMapper.map(updateDto, Counters.class));
        return modelMapper.map(result, CounterDto.class);
    }

    @KafkaSparuralMapping("counters/delete")
    public Boolean delete(@RequestParam Long id) {
        return counterService.delete(id);
    }

    @KafkaSparuralMapping("counters/import")
    public void importCounterForUser(@RequestParam Long loymaxUserId,
                                     @RequestParam Integer loymaxCounterId,
                                     @RequestParam Long counterId) {
        counterService.importAndBindForUser(loymaxUserId, loymaxCounterId, counterId);
    }

}
