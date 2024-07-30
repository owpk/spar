package ru.sparural.engine.entity.file;

import lombok.Data;
import ru.sparural.engine.api.enums.FileDocumentTypeField;

import java.util.UUID;


@Data
public class FileDocumentEntity {
    private Long id;
    private UUID fileId;
    private Long documentId;
    private FileDocumentTypeField field;
}
