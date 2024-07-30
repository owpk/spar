package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.UserRequestsSubjectsDto;
import ru.sparural.engine.services.UserRequestsSubjectsService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class UserRequestsSubjectsController {
    private final UserRequestsSubjectsService userRequestsSubjectsService;
    private final DtoMapperUtils dtoMapperUtils;

    @KafkaSparuralMapping("user-requests-subjects/index")
    public List<UserRequestsSubjectsDto> get(@RequestParam Integer offset,
                                             @RequestParam Integer limit) {
        return dtoMapperUtils.convertList(UserRequestsSubjectsDto.class, () ->
                userRequestsSubjectsService.getList(offset, limit));
    }

    @KafkaSparuralMapping("user-requests-subjects/create")
    public UserRequestsSubjectsDto create(@Payload UserRequestsSubjectsDto userRequestsSubjectsDto) {
        return userRequestsSubjectsService.create(userRequestsSubjectsDto);
    }

    @KafkaSparuralMapping("user-requests-subjects/delete")
    public Boolean delete(@RequestParam Long id) {
        return userRequestsSubjectsService.delete(id);
    }

    @KafkaSparuralMapping("user-requests-subjects/get")
    public UserRequestsSubjectsDto get(@RequestParam Long id) {
        return userRequestsSubjectsService.get(id);
    }

    @KafkaSparuralMapping("user-requests-subjects/update")
    public UserRequestsSubjectsDto update(@RequestParam Long id, @Payload UserRequestsSubjectsDto userRequestsSubjectsDto) {
        return userRequestsSubjectsService.update(id, userRequestsSubjectsDto);
    }
}
