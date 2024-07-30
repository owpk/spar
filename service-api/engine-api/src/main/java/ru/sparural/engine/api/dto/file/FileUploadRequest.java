package ru.sparural.engine.api.dto.file;

import lombok.Data;

import java.util.List;

@Data
public class FileUploadRequest {
    private String source;
    private Object sourceParameters;
    private List<FileDocumentDto> entities;
    private String mime;
    private String ext;
    private String name;
}
