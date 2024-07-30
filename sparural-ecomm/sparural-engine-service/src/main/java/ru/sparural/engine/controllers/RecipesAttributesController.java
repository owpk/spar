package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import ru.sparural.engine.api.dto.recipes.RecipeAttributesDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.RecipeAttributeEntity;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.RecipeAttributesService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class RecipesAttributesController {

    private final RecipeAttributesService recipeAttributesService;
    private final FileDocumentService fileDocumentService;
    private final ModelMapper modelMapper;

    @KafkaSparuralMapping("recipe-attributes/index")
    public List<RecipeAttributesDto> list(@RequestParam Integer offset,
                                          @RequestParam Integer limit) {
        var result = recipeAttributesService.index(offset, limit);
        var list = result.stream()
                .map(attr -> modelMapper.map(attr, RecipeAttributesDto.class))
                .collect(Collectors.toList());
        list.forEach(this::setIcon);
        return list;
    }

    @KafkaSparuralMapping("recipe-attributes/get")
    public RecipeAttributesDto get(@RequestParam Long id) {
        var result = recipeAttributesService.get(id);
        var dto = modelMapper.map(result, RecipeAttributesDto.class);
        setIcon(dto);
        return dto;
    }

    private void setIcon(RecipeAttributesDto dto) {
        var icon = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.RECIPE_ATTRIBUTE_ICON, dto.getId());
        if (!icon.isEmpty())
            dto.setIcon(icon.get(icon.size() - 1));
    }

    @KafkaSparuralMapping("recipe-attributes/create")
    public RecipeAttributesDto create(@Payload RecipeAttributesDto data) {
        var result = recipeAttributesService.create(modelMapper.map(data, RecipeAttributeEntity.class));
        return modelMapper.map(result, RecipeAttributesDto.class);
    }

    @KafkaSparuralMapping("recipe-attributes/update")
    public RecipeAttributesDto update(@Payload RecipeAttributesDto updateDto, @RequestParam Long id) {
        var result = recipeAttributesService.update(id, modelMapper.map(updateDto, RecipeAttributeEntity.class));
        return modelMapper.map(result, RecipeAttributesDto.class);
    }

    @KafkaSparuralMapping("recipe-attributes/delete")
    public Boolean delete(@RequestParam Long id) {
        return recipeAttributesService.delete(id);
    }

}
