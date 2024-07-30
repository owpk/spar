package ru.sparural.engine.controllers;


import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.engine.services.MerchantCommentsAnswersService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class MerchantCommentsAnswersController {

    private final MerchantCommentsAnswersService merchantCommentsAnswersService;

    @KafkaSparuralMapping("merchant-comments-answers/update")
    public AnswerDTO update(@RequestParam String code,
                            @RequestParam Long answerId,
                            @Payload AnswerDTO answerDTO) {
        return merchantCommentsAnswersService.update(code, answerId, answerDTO);
    }

    @KafkaSparuralMapping("merchant-comments-answers/delete")
    public Boolean delete(@RequestParam String code,
                          @RequestParam Long answerId) {
        return merchantCommentsAnswersService.delete(code, answerId);
    }

    @KafkaSparuralMapping("merchant-comments-answers/create")
    public AnswerDTO create(@RequestParam String code,
                            @Payload AnswerDTO answerDTO) {
        return merchantCommentsAnswersService.create(code, answerDTO);
    }
}
