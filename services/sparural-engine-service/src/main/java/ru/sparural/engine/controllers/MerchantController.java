package ru.sparural.engine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.MerchantDto;
import ru.sparural.engine.api.dto.MerchantUpdateDto;
import ru.sparural.engine.api.dto.merchant.Merchants;
import ru.sparural.engine.services.MerchantService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import javax.xml.bind.ValidationException;
import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class MerchantController {

    private final MerchantService service;
    private final ObjectMapper mapper;

    @KafkaSparuralMapping("merchants/index")
    public List<Merchants> list(@RequestParam Integer offset,
                                @RequestParam Integer limit,
                                @RequestParam Double topLeftLongitude,
                                @RequestParam Double topLeftLatitude,
                                @RequestParam Double bottomRightLongitude,
                                @RequestParam Double bottomRightLatitude,
                                @RequestParam Double userLongitude,
                                @RequestParam Double userLatitude,
                                @RequestParam String status,
                                @RequestParam String format,
                                @RequestParam String attributes,
                                @RequestParam Long userId,
                                @RequestParam List<String> userRoles) throws JsonProcessingException {
        return service.list(offset,
                limit,
                topLeftLongitude,
                topLeftLatitude,
                bottomRightLongitude,
                bottomRightLatitude,
                userLongitude,
                userLatitude,
                status,
                mapper.readValue(format, new TypeReference<>() {
                }),
                mapper.readValue(attributes, new TypeReference<>() {
                }),
                userId,
                userRoles);
    }


    @KafkaSparuralMapping("merchants/get")
    public Merchants get(@RequestParam Long id, @RequestParam Long userId) {
        return service.get(id, userId);
    }

    @KafkaSparuralMapping("merchants/create")
    public Merchants create(@Payload MerchantDto createMerchantRequestDto) throws ValidationException {
        return service.saveOrUpdate(createMerchantRequestDto);
    }

    @KafkaSparuralMapping("merchants/update")
    public Merchants update(@RequestParam Long id,
                            @Payload MerchantUpdateDto updateDto) throws ValidationException {
        return service.update(id, updateDto);
    }

    @KafkaSparuralMapping("merchants/delete")
    public Boolean delete(@RequestParam Long id) {
        return service.delete(id);
    }
}