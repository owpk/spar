package ru.sparural.engine.controllers;


import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.MerchantCommentsQuestionDTO;
import ru.sparural.engine.services.MerchantCommentsQuestionsService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class MerchantCommentsQuestionsController {

    private final MerchantCommentsQuestionsService merchantCommentsQuestionsService;

    @KafkaSparuralMapping("merchant-comments-questions/index")
    public List<MerchantCommentsQuestionDTO> list(@RequestParam Integer offset,
                                                  @RequestParam Integer limit) {
        return merchantCommentsQuestionsService.list(offset, limit);
    }

    @KafkaSparuralMapping("merchant-comments-questions/delete")
    public Boolean delete(@RequestParam String code) {

        return merchantCommentsQuestionsService.delete(code);
    }

    @KafkaSparuralMapping("merchant-comments-questions/update")
    public MerchantCommentsQuestionDTO update(@RequestParam String code,
                                              @Payload MerchantCommentsQuestionDTO merchantCommentsQuestionDTO) {
        return merchantCommentsQuestionsService.update(code, merchantCommentsQuestionDTO);
    }

    @KafkaSparuralMapping("merchant-comments-questions/create")
    public MerchantCommentsQuestionDTO create(@Payload MerchantCommentsQuestionDTO merchantCommentsQuestionDTO) {
        return merchantCommentsQuestionsService.create(merchantCommentsQuestionDTO);
    }

}
