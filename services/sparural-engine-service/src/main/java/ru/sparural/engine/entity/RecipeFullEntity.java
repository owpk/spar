package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class RecipeFullEntity {
    private Long id;
    private String title;
    private String description;
    private Integer calories;
    private Integer proteins;
    private Integer fats;
    private Integer carbohydrates;
    private Boolean draft;
    private List<RecipeAttributeEntity> attributes;
    private List<GoodsEntity> goods;
}
