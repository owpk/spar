package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GoodsEntity {
    private Long id;
    private String extGoodsId;
    private String name;
    private String description;
    private Boolean draft;
}
