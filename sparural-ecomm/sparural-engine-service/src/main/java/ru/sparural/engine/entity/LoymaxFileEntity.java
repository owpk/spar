package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoymaxFileEntity {
    private Long id;
    private String loymaxFileId;
    private String fileUuid;
}
