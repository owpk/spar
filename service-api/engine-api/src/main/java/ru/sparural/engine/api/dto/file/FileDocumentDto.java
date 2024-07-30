package ru.sparural.engine.api.dto.file;

import lombok.*;
import ru.sparural.engine.api.enums.FileDocumentTypeField;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDocumentDto {
    private UUID fileId;
    private FileDocumentTypeField field;
    private Long documentId;
}