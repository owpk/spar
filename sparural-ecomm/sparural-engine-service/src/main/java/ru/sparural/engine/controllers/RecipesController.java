package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.recipes.CreateRecipeDto;
import ru.sparural.engine.api.dto.recipes.RecipeAttributesDto;
import ru.sparural.engine.api.dto.recipes.RecipeDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.RecipeAttributeEntity;
import ru.sparural.engine.entity.RecipeEntity;
import ru.sparural.engine.entity.RecipeFullEntity;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.RecipeService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class RecipesController {

    private final RecipeService recipeService;
    private final FileDocumentService fileDocumentService;
    private final ModelMapper modelMapper;

    @KafkaSparuralMapping("recipes/index")
    public List<RecipeDto> list(@RequestParam Integer offset,
                                @RequestParam Integer limit) {
        List<RecipeFullEntity> result = recipeService.index(offset, limit);
        var list = result.stream()
                .map(rec -> {
                    RecipeDto recipeDto = modelMapper.map(rec, RecipeDto.class);
                    setIcon(recipeDto.getAttributes());

                    return recipeDto;
                })
                .collect(Collectors.toList());
        list.forEach(this::setFiles);
        return list;
    }

    private void setIcon(List<RecipeAttributesDto> entities) {
        for(var entity : entities) {
            var icon = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.RECIPE_ATTRIBUTE_ICON, entity.getId());
            if (!icon.isEmpty())
                entity.setIcon(icon.get(icon.size() - 1));
        }
    }

    private void setFiles(RecipeDto dto) {
        Function<List<FileDto>, FileDto> func = files -> files.isEmpty() ? null : files.get(files.size() - 1);
        var photos = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.RECIPE_PHOTO, dto.getId());
        var previews = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.RECIPE_PREVIEW, dto.getId());
        dto.setPhoto(func.apply(photos));
        dto.setPreview(func.apply(previews));

        dto.getGoods().forEach(good -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.GOODS_PHOTO, good.getId());
            if (!files.isEmpty()) {
                good.setPhoto(files.get(files.size() - 1));
            }
        });

        dto.getGoods().forEach(good -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.GOODS_PREVIEW, good.getId());
            if (!files.isEmpty()) {
                good.setPreview(files.get(files.size() - 1));
            }
        });
    }

    @KafkaSparuralMapping("recipes/get")
    public RecipeDto get(@RequestParam Long id) {
        var result = recipeService.get(id);
        var dto = modelMapper.map(result, RecipeDto.class);
        setFiles(dto);
        return dto;
    }

    @KafkaSparuralMapping("recipes/create")
    public RecipeDto create(@Payload CreateRecipeDto data) {
        var result = recipeService
                .create(data.getGoods(),
                        data.getAttributes(),
                        modelMapper.map(data, RecipeEntity.class));
        return modelMapper.map(result, RecipeDto.class);
    }

    @KafkaSparuralMapping("recipes/update")
    public RecipeDto update(@Payload CreateRecipeDto updateDto, @RequestParam Long id) {
        var result = recipeService.update(id, modelMapper.map(updateDto, RecipeEntity.class),
                updateDto.getGoods(), updateDto.getAttributes());
        return modelMapper.map(result, RecipeDto.class);
    }

    @KafkaSparuralMapping("recipes/delete")
    public Boolean delete(@RequestParam Long id) {
        return recipeService.delete(id);
    }

}
