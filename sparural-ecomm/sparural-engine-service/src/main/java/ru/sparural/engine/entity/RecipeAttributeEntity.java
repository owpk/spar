package ru.sparural.engine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import ru.sparural.engine.api.dto.FileDto;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class RecipeAttributeEntity {
    private Long id;
    private String name;
    private Boolean showOnPreview;
    private Boolean draft;
}
