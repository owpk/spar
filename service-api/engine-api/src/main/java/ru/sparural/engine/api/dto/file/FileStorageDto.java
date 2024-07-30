package ru.sparural.engine.api.dto.file;

import lombok.Data;

import java.util.UUID;

@Data
public class FileStorageDto {
    private UUID id;
    private Long size;
    private Long used;
    private String url;
}
