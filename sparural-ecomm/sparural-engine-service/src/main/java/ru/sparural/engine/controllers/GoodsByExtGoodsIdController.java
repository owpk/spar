package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
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
public class GoodsByExtGoodsIdController {
    private final GoodsService goodsService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("goods-by-ext-goods-id/get")
    public GoodsForAdminDto get(@RequestParam String goodsId) throws ResourceNotFoundException {
        var dto = goodsService.get(goodsId);
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

    @KafkaSparuralMapping("goods-by-ext-goods-id/update")
    public GoodsForAdminDto update(@RequestParam String goodsId,
                                   @Payload GoodsForAdminUpdateDto goodsForAdminDto) {
        return goodsService.update(goodsId, goodsForAdminDto);
    }

    @KafkaSparuralMapping("goods-by-ext-goods-id/delete")
    public Boolean delete(@RequestParam String id) {
        return goodsService.delete(id);
    }

}
