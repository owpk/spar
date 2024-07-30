package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class RecipeEntity {
    private Long id;
    private String title;
    private String description;
    private Integer calories;
    private Integer proteins;
    private Integer fats;
    private Integer carbohydrates;
    private Boolean draft;
}
