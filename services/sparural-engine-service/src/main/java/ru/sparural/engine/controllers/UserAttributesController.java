package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import ru.sparural.engine.api.dto.user.UserAttributesDto;
import ru.sparural.engine.entity.UserAttributesEntity;
import ru.sparural.engine.services.UserAttributesService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class UserAttributesController {
    private final UserAttributesService userAttributesService;
    private final ModelMapper modelMapper;

    @KafkaSparuralMapping("user-attributes/index")
    public List<UserAttributesDto> list(@RequestParam Integer offset,
                                        @RequestParam Integer limit) {
        var result = userAttributesService.index(offset, limit);
        return result.stream()
                .map(attr -> modelMapper.map(attr, UserAttributesDto.class))
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("user-attributes/get")
    public UserAttributesDto get(@RequestParam Long id) {
        var result = userAttributesService.get(id);
        return modelMapper.map(result, UserAttributesDto.class);
    }

    @KafkaSparuralMapping("user-attributes/create")
    public UserAttributesDto create(@Payload UserAttributesDto data) {
        var result = userAttributesService.create(modelMapper.map(data, UserAttributesEntity.class));
        return modelMapper.map(result, UserAttributesDto.class);
    }

    @KafkaSparuralMapping("user-attributes/update")
    public UserAttributesDto update(@Payload UserAttributesDto updateDto, @RequestParam Long id) {
        var result = userAttributesService.update(id, modelMapper.map(updateDto, UserAttributesEntity.class));
        return modelMapper.map(result, UserAttributesDto.class);
    }

    @KafkaSparuralMapping("user-attributes/delete")
    public Boolean delete(@RequestParam Long id) {
        return userAttributesService.delete(id);
    }

}
