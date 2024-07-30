package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

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
