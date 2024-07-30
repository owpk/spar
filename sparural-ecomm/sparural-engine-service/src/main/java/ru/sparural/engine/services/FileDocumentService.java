package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.file.FileDocumentEntity;
import ru.sparural.engine.entity.file.FileEntity;
import ru.sparural.engine.entity.file.FileStorageEntity;

import java.util.List;
import java.util.UUID;

public interface FileDocumentService {

    List<FileDocumentEntity> updateFileDocuments(Long userId, UUID fileId, List<FileDocumentEntity> documents);

    FileInfoDto createFile(FileInfoDto fileInfo, Long userId);

    FileEntity getFile(UUID fileId);

    List<FileDocumentEntity> getFileDocumentsForFile(UUID fileId, Long userId);

    FileStorageEntity getFileStorage(UUID fileStorageId);

    List<FileDto> getFileByDocumentId(FileDocumentTypeField field, Long documentId);

    void deleteFile(Long userId, UUID fileId);
}
