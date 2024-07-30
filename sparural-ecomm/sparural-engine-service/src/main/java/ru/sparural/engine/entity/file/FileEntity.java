package ru.sparural.engine.entity.file;

import lombok.Data;

import java.util.UUID;

@Data
public class FileEntity {
    private UUID id;
    private UUID storageId;
    private String name;
    private String ext;
    private Long size;
    private String mime;
}
