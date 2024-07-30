package ru.sparural.engine.repositories;

import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.api.enums.FileDocumentTypes;
import ru.sparural.engine.entity.file.FileDocumentEntity;
import ru.sparural.engine.entity.file.FileEntity;
import ru.sparural.engine.entity.file.FileStorageEntity;
import ru.sparural.engine.entity.file.FileWithStorageEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FileDocumentRepository {

    FileEntity createFile(FileEntity entity);

    FileStorageEntity findFileStorageById(UUID storageId);

    FileStorageEntity updateFileStorageSizeById(UUID storageId, Long diff);

    List<FileDocumentEntity> findFileDocumentsByIdAndType(Long documentId, FileDocumentTypeField fieldType);

    FileDocumentEntity updateFileDocument(Long id, FileDocumentEntity document);

    FileDocumentEntity insertFileDocument(FileDocumentEntity document);

    void removeFileDocument(Long documentId);

    void removeFileById(UUID fileId);

    FileEntity findFileById(UUID fileId);

    List<FileDocumentEntity> findFileDocumentsByFileId(UUID fileId);

    Set<Long> getDocumentOwner(FileDocumentTypes documentType, Long documentId);

    boolean isDocumentExist(FileDocumentTypes documentType, Long documentId);

    List<FileWithStorageEntity> findFileWithStorageByDocumentField(FileDocumentTypeField field, Long documentId);
}
