package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;
import ru.sparural.engine.api.dto.FileDto;

@Data
@ToString
public class AttributeEntity {
    private Long id;
    private String name;
    private FileDto icon;
    private Boolean draft;
}
