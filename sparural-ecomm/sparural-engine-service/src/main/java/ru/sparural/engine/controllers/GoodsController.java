package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminCreateDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminUpdateDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.GoodsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class GoodsController {
    private final GoodsService goodsService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("goods/index")
    public List<GoodsForAdminDto> list(
            @RequestParam String search,
            @RequestParam Integer offset,
            @RequestParam Integer limit) {
        var values = goodsService.list(offset, limit, search);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.GOODS_PHOTO, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
        });
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.GOODS_PREVIEW, dto.getId());
            if (!files.isEmpty()) {
                dto.setPreview(files.get(files.size() - 1));
            }
        });
        return values;
    }

    @KafkaSparuralMapping("goods/get")
    public GoodsForAdminDto get(@RequestParam Long id) throws ResourceNotFoundException {
        var dto = goodsService.get(id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.GOODS_PHOTO, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }
        List<FileDto> previews = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.GOODS_PREVIEW, dto.getId());
        if (!previews.isEmpty()) {
            dto.setPreview(previews.get(files.size() - 1));
        }
        return dto;
    }

    @KafkaSparuralMapping("goods/create")
    public GoodsForAdminDto create(@Payload GoodsForAdminCreateDto goodsForAdminDto) {
        return goodsService.create(goodsForAdminDto);
    }

    @KafkaSparuralMapping("goods/update")
    public GoodsForAdminDto update(@RequestParam Long id,
                                   @Payload GoodsForAdminUpdateDto goodsForAdminDto) {
        return goodsService.update(id, goodsForAdminDto);
    }

    @KafkaSparuralMapping("goods/delete")
    public Boolean delete(@RequestParam Long id) {
        return goodsService.delete(id);
    }
}
