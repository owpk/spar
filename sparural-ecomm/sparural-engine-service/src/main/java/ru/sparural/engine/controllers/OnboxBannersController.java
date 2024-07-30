package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.MobileNavigateTargetDto;
import ru.sparural.engine.api.dto.OnboxBannerDto;
import ru.sparural.engine.api.dto.OnboxBannerForUpdateDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.OnboxBannersService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class OnboxBannersController {

    private final OnboxBannersService onboxBannersServiceInt;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("onbox-banners/create")
    public OnboxBannerDto create(@Payload OnboxBannerDto data) {
        return onboxBannersServiceInt.create(data);
    }

    @KafkaSparuralMapping("onbox-banners/update")
    public OnboxBannerDto update(@Payload OnboxBannerForUpdateDto updateDto, @RequestParam Long id) {
        //change OnboxBannerForUpdateDto to OnboxBannerDto, when change mobileNavigateTargetId to MobileNavigateTarget
        OnboxBannerDto data = new OnboxBannerDto();
        data.setId(updateDto.getId());
        data.setOrder(updateDto.getOrder());
        data.setCitySelect(updateDto.getCitySelect());
        data.setCities(updateDto.getCities());
        data.setDescription(updateDto.getDescription());
        data.setIsPublic(updateDto.getIsPublic());
        data.setDraft(updateDto.getDraft());
        data.setTitle(updateDto.getTitle());
        data.setPhoto(updateDto.getPhoto());
        data.setUrl(updateDto.getUrl());
        data.setDateStart(updateDto.getDateStart());
        data.setDateEnd(updateDto.getDateEnd());
        MobileNavigateTargetDto mobileNavigateTargetDto = new MobileNavigateTargetDto();
        mobileNavigateTargetDto.setId(updateDto.getMobileNavigateTargetId());
        data.setMobileNavigateTarget(mobileNavigateTargetDto);
        return onboxBannersServiceInt.update(id, data);
    }

    @KafkaSparuralMapping("onbox-banners/list")
    public List<OnboxBannerDto> list(@RequestParam Integer offset,
                                     @RequestParam Integer limit,
                                     @RequestParam Long city,
                                     @RequestParam Boolean showPublic,
                                     @RequestParam Long dateStart,
                                     @RequestParam Long dateEnd) {
        List<OnboxBannerDto> values = onboxBannersServiceInt.list(offset, limit, city,
                showPublic, dateStart, dateEnd);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.ONBOX_BANNER_PHOTO, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
        });
        return values.stream().sorted(
                Comparator.comparing(x -> {
                    if (x.getOrder() != null) {
                        return x.getOrder();
                    }
                    return 0;
                })).collect(Collectors.toList());
    }

    @KafkaSparuralMapping("onbox-banners/get")
    public OnboxBannerDto get(@RequestParam Long id) {
        OnboxBannerDto dto = onboxBannersServiceInt.get(id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.ONBOX_BANNER_PHOTO, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }
        return dto;
    }

    @KafkaSparuralMapping("onbox-banners/delete")
    public Boolean delete(@RequestParam Long id) {
        return onboxBannersServiceInt.delete(id);
    }

}
