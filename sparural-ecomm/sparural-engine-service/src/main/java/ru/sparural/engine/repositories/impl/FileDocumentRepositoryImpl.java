package ru.sparural.engine.repositories.impl;


import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.api.enums.FileDocumentTypes;
import ru.sparural.engine.api.enums.FileDocumentUsers;
import ru.sparural.engine.entity.file.FileDocumentEntity;
import ru.sparural.engine.entity.file.FileEntity;
import ru.sparural.engine.entity.file.FileStorageEntity;
import ru.sparural.engine.entity.file.FileWithStorageEntity;
import ru.sparural.engine.repositories.FileDocumentRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.FileDocument;
import ru.sparural.tables.FileStorages;
import ru.sparural.tables.Files;
import ru.sparural.tables.records.FileDocumentRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FileDocumentRepositoryImpl implements FileDocumentRepository {
    private final DSLContext dslContext;

    @Override
    public FileEntity createFile(FileEntity entity) {
        Long now = TimeHelper.currentTime();
        return dslContext.insertInto(Files.FILES)
                .set(Files.FILES.ID, entity.getId())
                .set(Files.FILES.STORAGE_ID, entity.getStorageId())
                .set(Files.FILES.SIZE, entity.getSize())
                .set(Files.FILES.EXT, entity.getExt())
                .set(Files.FILES.MIME, entity.getMime())
                .set(Files.FILES.NAME, entity.getName())
                .set(Files.FILES.CREATED_AT, now)
                .set(Files.FILES.UPDATED_AT, now)
                .returning().fetchOneInto(FileEntity.class);
    }

    @Override
    public FileStorageEntity findFileStorageById(UUID storageId) {
        return dslContext.selectFrom(FileStorages.FILE_STORAGES)
                .where(FileStorages.FILE_STORAGES.ID.eq(storageId))
                .fetchOptionalInto(FileStorageEntity.class)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Storage with id '%s' does not exist", storageId.toString())));
    }

    @Override
    public FileStorageEntity updateFileStorageSizeById(UUID storageId, Long diff) {
        return dslContext.update(FileStorages.FILE_STORAGES)
                .set(FileStorages.FILE_STORAGES.USED, FileStorages.FILE_STORAGES.USED.add(diff))
                .where(FileStorages.FILE_STORAGES.ID.eq(storageId))
                .returning().fetchOneInto(FileStorageEntity.class);
    }

    @Override
    public List<FileDocumentEntity> findFileDocumentsByIdAndType(Long documentId, FileDocumentTypeField fieldType) {
        return dslContext.selectFrom(FileDocument.FILE_DOCUMENT)
                .where(FileDocument.FILE_DOCUMENT.DOCUMENT_ID.eq(documentId))
                .and(FileDocument.FILE_DOCUMENT.FIELD.eq(fieldType.name()))
                .fetch(this::mapToFileDocumentEntity);
    }

    @Override
    public FileDocumentEntity updateFileDocument(Long id, FileDocumentEntity document) {
        return dslContext.update(FileDocument.FILE_DOCUMENT)
                .set(FileDocument.FILE_DOCUMENT.DOCUMENT_ID, document.getDocumentId())
                .set(FileDocument.FILE_DOCUMENT.FIELD, document.getField().name())
                .set(FileDocument.FILE_DOCUMENT.FILE_ID, document.getFileId())
                .set(FileDocument.FILE_DOCUMENT.UPDATED_AT, TimeHelper.currentTime())
                .where(FileDocument.FILE_DOCUMENT.ID.eq(id))
                .returning().fetchOne(this::mapToFileDocumentEntity);
    }

    @Override
    public FileDocumentEntity insertFileDocument(FileDocumentEntity document) {
        Long now = TimeHelper.currentTime();
        return dslContext.insertInto(FileDocument.FILE_DOCUMENT)
                .set(FileDocument.FILE_DOCUMENT.DOCUMENT_ID, document.getDocumentId())
                .set(FileDocument.FILE_DOCUMENT.FIELD, document.getField().name())
                .set(FileDocument.FILE_DOCUMENT.FILE_ID, document.getFileId())
                .set(FileDocument.FILE_DOCUMENT.UPDATED_AT, now)
                .set(FileDocument.FILE_DOCUMENT.CREATED_AT, now)
                .returning().fetchOne(this::mapToFileDocumentEntity);
    }

    private FileDocumentEntity mapToFileDocumentEntity(FileDocumentRecord record) {
        FileDocumentEntity entity = new FileDocumentEntity();
        entity.setDocumentId(record.getDocumentId());
        entity.setField(FileDocumentTypeField.valueOf(record.getField()));
        entity.setFileId(record.getFileId());
        entity.setId(record.getId());
        return entity;
    }

    @Override
    public FileEntity findFileById(UUID fileId) {
        return dslContext.selectFrom(Files.FILES)
                .where(Files.FILES.ID.eq(fileId))
                .fetchOptionalInto(FileEntity.class)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("File with id '%s' does not exist", fileId.toString())));
    }

    @Override
    public List<FileDocumentEntity> findFileDocumentsByFileId(UUID fileId) {
        return dslContext.selectFrom(FileDocument.FILE_DOCUMENT)
                .where(FileDocument.FILE_DOCUMENT.FILE_ID.eq(fileId))
                .fetch(this::mapToFileDocumentEntity);
    }

    @Override
    public Set<Long> getDocumentOwner(FileDocumentTypes documentType, Long documentId) {

        FileDocumentUsers usersMeta = FileDocumentUsers.of(documentType.name());
        if (usersMeta == null) {
            return Collections.emptySet();
        }

        if (StringUtils.isEmpty(usersMeta.getTable())) {
            return getDocumentOwnerFromDocumentTable(usersMeta, documentType, documentId);
        }

        return getDocumentOwnerFromUsersTable(usersMeta, documentType, documentId);
    }

    private Set<Long> getDocumentOwnerFromDocumentTable(FileDocumentUsers usersMeta, FileDocumentTypes documentType, Long documentId) {
        Field<Long> loginIdField = DSL.field(String.format("%s.\"%s\"",
                documentType.getTable(), usersMeta.getUserIdField()), Long.class);
        Field<Long> documentIdField = DSL.field("id", Long.class);
        return dslContext.select(loginIdField)
                .from(documentType.getTable())
                .where(documentIdField.eq(documentId))
                .fetchInto(Long.class)
                .stream()
                .collect(Collectors.toSet());

    }

    private Set<Long> getDocumentOwnerFromUsersTable(FileDocumentUsers usersMeta, FileDocumentTypes documentType, Long documentId) {
        Field<Long> loginIdField = DSL.field(String.format("%s.\"%s\"",
                usersMeta.getTable(), usersMeta.getUserIdField()), Long.class);
        Field<Long> documentIdField = DSL.field(String.format("%s.\"id\"", documentType.getTable()), Long.class);
        return dslContext.select(loginIdField)
                .from(documentType.getTable())
                .leftJoin(usersMeta.getTable())
                .on(String.format("%s.id = %s.\"%s\"",
                        documentType.getTable(), usersMeta.getTable(), usersMeta.getDocumentIdField()))
                .where(documentIdField.eq(documentId))
                .fetchInto(Long.class)
                .stream()
                .collect(Collectors.toSet());

    }

    @Override
    public boolean isDocumentExist(FileDocumentTypes documentType, Long documentId) {
        Field<Long> documentIdField = DSL.field("id", Long.class);
        int count = dslContext.selectCount()
                .from(documentType.getTable())
                .where(documentIdField.eq(documentId))
                .fetchOneInto(Integer.class);

        return count > 0;
    }

    @Override
    public List<FileWithStorageEntity> findFileWithStorageByDocumentField(FileDocumentTypeField field, Long documentId) {
        return dslContext.select(
                        Files.FILES.ID.as("fileId"),
                        Files.FILES.MIME,
                        Files.FILES.EXT,
                        Files.FILES.SIZE.as("file_size"),
                        Files.FILES.NAME,
                        FileStorages.FILE_STORAGES.ID.as("storage_id"),
                        FileStorages.FILE_STORAGES.SIZE.as("storage_size"),
                        FileStorages.FILE_STORAGES.USED,
                        FileStorages.FILE_STORAGES.URL
                ).from(FileDocument.FILE_DOCUMENT)
                .leftJoin(Files.FILES)
                .on(FileDocument.FILE_DOCUMENT.FILE_ID.eq(Files.FILES.ID))
                .leftJoin(FileStorages.FILE_STORAGES)
                .on(Files.FILES.STORAGE_ID.eq(FileStorages.FILE_STORAGES.ID))
                .where(FileDocument.FILE_DOCUMENT.FIELD.eq(field.name()))
                .and(FileDocument.FILE_DOCUMENT.DOCUMENT_ID.eq(documentId))
                .fetch(this::mapRecordToFileWithStorageEntity);
    }

    private FileWithStorageEntity mapRecordToFileWithStorageEntity(Record record) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(record.getValue("fileId", UUID.class));
        fileEntity.setExt(record.getValue(Files.FILES.EXT, String.class));
        fileEntity.setMime(record.getValue(Files.FILES.MIME, String.class));
        fileEntity.setName(record.getValue(Files.FILES.NAME, String.class));
        fileEntity.setSize(record.getValue("file_size", Long.class));

        FileStorageEntity storageEntity = new FileStorageEntity();
        storageEntity.setId(record.getValue("storage_id", UUID.class));
        storageEntity.setSize(record.getValue("storage_size", Long.class));
        storageEntity.setUsed(record.getValue(FileStorages.FILE_STORAGES.USED, Long.class));
        storageEntity.setUrl(record.getValue(FileStorages.FILE_STORAGES.URL, String.class));

        FileWithStorageEntity result = new FileWithStorageEntity();
        result.setFile(fileEntity);
        result.setStorage(storageEntity);

        return result;
    }

    @Override
    public void removeFileDocument(Long documentId) {
        dslContext.deleteFrom(FileDocument.FILE_DOCUMENT)
                .where(FileDocument.FILE_DOCUMENT.ID.eq(documentId))
                .execute();
    }

    @Override
    public void removeFileById(UUID fileId) {
        dslContext.deleteFrom(Files.FILES)
                .where(Files.FILES.ID.eq(fileId))
                .execute();
    }
}
