package ru.sparural.engine.entity.file;

import lombok.Data;

import java.util.UUID;

@Data
public class FileStorageEntity {
    private UUID id;
    private String url;
    private Long size;
    private Long used;

}
