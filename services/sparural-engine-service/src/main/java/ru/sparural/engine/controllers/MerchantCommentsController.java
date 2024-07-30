package ru.sparural.engine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.MerchantCommentDto;
import ru.sparural.engine.api.dto.MerchantCommentsDto;
import ru.sparural.engine.services.MerchantCommentsService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;


@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class MerchantCommentsController {

    private final MerchantCommentsService merchantCommentsService;
    private final ObjectMapper mapper;

    @KafkaSparuralMapping("merchant-comments/create")
    public Boolean create(@Payload MerchantCommentsDto merchantCommentsDto,
                          @RequestParam Long userId) {
        return merchantCommentsService.create(merchantCommentsDto, userId);
    }

    @KafkaSparuralMapping("merchant-comments/index")
    public List<MerchantCommentDto> list(@RequestParam Integer offset,
                                         @RequestParam Integer limit,
                                         @RequestParam String search,
                                         @RequestParam String grade,
                                         @RequestParam Long dateTimeStart,
                                         @RequestParam Long dateTimeEnd,
                                         @RequestParam String merchantId) throws JsonProcessingException {

        return merchantCommentsService.list(offset,
                limit,
                search,
                mapper.readValue(grade, new TypeReference<>() {
                }),
                dateTimeStart,
                dateTimeEnd,
                mapper.readValue(merchantId, new TypeReference<>() {
                }));
    }

    @KafkaSparuralMapping("merchant-comments/get")
    public MerchantCommentDto get(@RequestParam Long id) {
        return merchantCommentsService.get(id);
    }

}