package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.file.FileDocumentDto;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.engine.api.dto.file.FileStorageDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.file.FileDocumentEntity;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class FileController {

    private final FileDocumentService fileDocumentService;
    private final DtoMapperUtils dtoMapperUtils;

    @KafkaSparuralMapping("file/create")
    public FileInfoDto createFile(@RequestParam Long userId, @Payload FileInfoDto fileInfo) {
        return fileDocumentService.createFile(fileInfo, userId);
    }

    @KafkaSparuralMapping("file/documents/update")
    public List<FileDocumentDto> updateFileDocuments(@RequestParam Long userId, @RequestParam("fileId") String fileIdString, @Payload List<FileDocumentDto> documents) {
        UUID fileId = UUID.fromString(fileIdString);
        List<FileDocumentEntity> entities = dtoMapperUtils.convertList(FileDocumentEntity.class, documents);
        return dtoMapperUtils.convertList(FileDocumentDto.class, fileDocumentService.updateFileDocuments(userId, fileId, entities));
    }

    @KafkaSparuralMapping("file/info")
    public FileInfoDto getFileInfo(@RequestParam Long userId, @Payload String fileIdStr) {
        UUID fileId = UUID.fromString(fileIdStr);
        FileInfoDto result = dtoMapperUtils.convert(fileDocumentService.getFile(fileId), FileInfoDto.class);
        result.setEntities(dtoMapperUtils.convertList(FileDocumentDto.class, fileDocumentService.getFileDocumentsForFile(fileId, userId)));
        return result;
    }

    @KafkaSparuralMapping("file/storage")
    public FileStorageDto getFileStorage(@Payload String fileStorageIdStr) {
        UUID fileStorageId = UUID.fromString(fileStorageIdStr);
        return dtoMapperUtils.convert(fileDocumentService.getFileStorage(fileStorageId), FileStorageDto.class);
    }

    @KafkaSparuralMapping("file/delete")
    public void deleteFile(@RequestParam Long userId, @RequestParam("fileId") String fileIdString) {
        UUID fileId = UUID.fromString(fileIdString);
        fileDocumentService.deleteFile(userId, fileId);
    }

    @KafkaSparuralMapping("file/get")
    public List<FileDto> getFileByType(@RequestParam String typeField, @RequestParam Long id) {
        var fileType = FileDocumentTypeField.valueOf(typeField);
        return fileDocumentService.getFileByDocumentId(fileType, id);
    }
}
