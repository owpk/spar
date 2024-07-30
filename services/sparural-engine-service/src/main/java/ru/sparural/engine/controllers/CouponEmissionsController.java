package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.CouponEmissionsDto;
import ru.sparural.engine.api.dto.CouponEmissionsRequestDto;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.CouponEmissionsService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class CouponEmissionsController {
    private final CouponEmissionsService couponEmissionsService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("coupon-emmissions/index")
    public List<CouponEmissionsDto> list(@RequestParam Integer offset,
                                         @RequestParam Integer limit) {
        var values = couponEmissionsService.getList(offset, limit);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.COUPON_EMISSION_PHOTO, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
        });

        return values;
    }

    @KafkaSparuralMapping("coupon-emmissions/get")
    public CouponEmissionsDto get(@RequestParam Long id) {
        var dto = couponEmissionsService.get(id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.COUPON_EMISSION_PHOTO, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }

        return dto;
    }

    @KafkaSparuralMapping("coupon-emissions/update")
    public CouponEmissionsDto update(@RequestParam Long id,
                                     @Payload CouponEmissionsRequestDto couponEmissionsDto) {
        return couponEmissionsService.update(id, couponEmissionsDto);
    }

    @KafkaSparuralMapping("coupon-emissions/delete")
    public Boolean delete(@RequestParam Long id) {
        return couponEmissionsService.delete(id);
    }
}
