package ru.sparural.engine.controllers;


import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FaqDTO;
import ru.sparural.engine.services.FaqService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class FaqController {


    private final FaqService faqService;

    @KafkaSparuralMapping("faq/index")
    public List<FaqDTO> list(@RequestParam Integer offset,
                             @RequestParam Integer limit) {
        return faqService.list(offset, limit);
    }

    @KafkaSparuralMapping("faq/get")
    public FaqDTO get(@RequestParam Long id) throws ResourceNotFoundException {
        return faqService.get(id);
    }


    @KafkaSparuralMapping("faq/update")
    public FaqDTO update(@RequestParam Long id, @Payload FaqDTO faqDTO) throws ResourceNotFoundException, ValidationException {
        return faqService.update(id, faqDTO);
    }

    @KafkaSparuralMapping("faq/delete")
    public Boolean delete(@RequestParam Long id) {
        return faqService.delete(id);
    }

    @KafkaSparuralMapping("faq/create")
    public FaqDTO create(@Payload FaqDTO faqDTO) {
        return faqService.create(faqDTO);
    }

}