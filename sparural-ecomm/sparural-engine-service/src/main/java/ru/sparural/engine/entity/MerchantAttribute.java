package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MerchantAttribute {
    private Long id;
    private String name;
    private Boolean draft;
}
