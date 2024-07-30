package ru.sparural.engine.api.dto.file;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FileInfoDto {
    private UUID id;
    private UUID storageId;
    private Long size;
    private String ext;
    private String mime;
    private String name;
    private List<FileDocumentDto> entities;
}
