package ru.sparural.engine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.CategoriesDto;
import ru.sparural.engine.api.dto.FavoriteCategoriesDataRequestDto;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.FavoriteCategoriesService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
@Slf4j
public class FavoriteCategoriesController {
    private final FavoriteCategoriesService favoriteCategoriesService;
    private final LoymaxService loymaxService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("favorite-categories/index")
    public CategoriesDto list(@RequestParam Long userId,
                              @RequestParam Integer offset,
                              @RequestParam Integer limit) throws JsonProcessingException {

        if (userId == 0) {
            var values = favoriteCategoriesService.list(offset, limit);
            values.getData().forEach(dto -> {
                List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.FAVORITE_CATEGORY_PHOTO, dto.getId());
                if (!files.isEmpty()) {
                    dto.setPhoto(files.get(files.size() - 1));
                }
            });
            return values;
        }
        try {
            var values = favoriteCategoriesService.list(userId, offset, limit);
            values.getData().forEach(dto -> {
                List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.FAVORITE_CATEGORY_PHOTO, dto.getId());
                if (!files.isEmpty()) {
                    dto.setPhoto(files.get(files.size() - 1));
                }
            });
            return values;
        } catch (Exception exception) {
            log.error("Select favorite categories exception: userid: {}", userId, exception);
            return CategoriesDto.builder().data(List.of()).build();
        }
    }


    @KafkaSparuralMapping("favorite-categories/get")
    public CategoryDto get(@RequestParam Long userId,
                           @RequestParam Long id) throws JsonProcessingException {

        if (userId == 0) {

            var dto = favoriteCategoriesService.get(id);
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.FAVORITE_CATEGORY_PHOTO, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
            return dto;
        }

        var dto = favoriteCategoriesService.get(userId, id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.FAVORITE_CATEGORY_PHOTO, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }
        return dto;
    }


    @KafkaSparuralMapping("favorite-categories/update")
    public CategoryDto update(@RequestParam Long id,
                              @Payload FavoriteCategoriesDataRequestDto dto) {
        return favoriteCategoriesService.update(id, dto);
    }

    @KafkaSparuralMapping("favorite-categories/delete")
    public Boolean delete(@RequestParam Long id) {
        return favoriteCategoriesService.delete(id);
    }

    @KafkaSparuralMapping("favorite-categories/select")
    public Boolean select(@RequestParam List<Integer> listToSelect,
                          @RequestParam Long userId) throws Exception {

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        List<String> ids = new ArrayList<>();
        for (Integer category : listToSelect) {
            ids.add(favoriteCategoriesService.findByIdWithGroupId(category.longValue()));
        }
        if (!ids.isEmpty()) {
            loymaxService.acceptToFavoriteCategory(loymaxUser, ids);
        }
        return true;
    }

}
