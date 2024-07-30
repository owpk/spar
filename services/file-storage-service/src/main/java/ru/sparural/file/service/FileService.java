package ru.sparural.file.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.file.FileDocumentDto;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.engine.api.dto.file.FileStorageDto;
import ru.sparural.file.dto.FileSourceParameters;
import ru.sparural.file.dto.FileSourceType;
import ru.sparural.file.exceptions.ApplicationException;
import ru.sparural.file.exceptions.RedirectException;
import ru.sparural.file.model.FileSourceInfo;
import ru.sparural.file.service.sources.FileSourceService;
import ru.sparural.file.service.store.FileStore;
import ru.sparural.file.utils.FileServerResponseHandler;
import ru.sparural.file.utils.FileServerUtils;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Data
public class FileService {

    private final FileSourceService fileSourceService;
    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final FileStore fileStore;
    @Value("${sparural.kafka.request-topics.engine}")
    private String dbTopic;
    @Value("#{T(java.util.UUID).fromString('${storage.id}')}")
    private UUID storageId;

    public List<FileDocumentDto> updateFile(Long userId, UUID fileId, List<FileDocumentDto> documents) {
        return kafkaRequestCreator.createRequestBuilder()
                .withAction("file/documents/update")
                .withTopicName(dbTopic)
                .withRequestParameter("userId", userId)
                .withRequestParameter("fileId", fileId)
                .withRequestBody(documents)
                .sendForEntity(FileServerResponseHandler.getInstance());
    }

    public FileInfoDto insertFile(FileSourceType source,
                                  String fileName,
                                  String ext,
                                  String mime,
                                  FileSourceParameters sourceParameters,
                                  List<FileDocumentDto> documents,
                                  InputStream file) {

        UUID newFileId = UUID.randomUUID();
        Long userId = -1L;
        try {
            userId = FileServerUtils.getUserId();
        } catch (NullPointerException e) { /*ignore*/ }
        FileInfoDto fileInfo = new FileInfoDto();
        fileInfo.setName(fileName);
        fileInfo.setMime(mime);
        fileInfo.setExt(ext);
        FileSourceInfo fileSourceInfo = fileSourceService.insertFile(newFileId, fileInfo, source, sourceParameters, file);
        try (InputStream is = fileSourceInfo.getFileInputStream()) {
            documents.forEach(document -> document.setFileId(newFileId));

            fileInfo = new FileInfoDto();
            fileInfo.setEntities(documents);
            fileInfo.setId(newFileId);
            fileInfo.setSize(fileStore.writeFileToTemporary(newFileId, is));
            fileInfo.setStorageId(storageId);
            fileInfo.setExt(ext);
            fileInfo.setMime(Optional.ofNullable(mime).orElse(fileInfo.getMime()));
            fileInfo.setName(fileName);

            FileInfoDto result = kafkaRequestCreator.createRequestBuilder()
                    .withRequestParameter("userId", userId)
                    .withAction("file/create")
                    .withTopicName(dbTopic)
                    .withRequestBody(fileInfo)
                    .sendForEntity(FileServerResponseHandler.getInstance());
            fileStore.moveFileFromTemporaryToStore(newFileId);
            return result;
        } catch (ApplicationException appEx) {
            log.error("", appEx);
            throw appEx;
        } catch (IOException | RuntimeException ex) {
            log.error("Error on insert file", ex);
            fileStore.deleteTemporaryFile(newFileId);
            throw new ApplicationException(ex);
        }
    }

    public FileInfoDto getFileInfo(UUID fileId, Long userId) {
        return kafkaRequestCreator.createRequestBuilder()
                .withAction("file/info")
                .withTopicName(dbTopic)
                .withRequestParameter("userId", userId)
                .withRequestBody(fileId.toString())
                .sendForEntity(FileServerResponseHandler.getInstance());
    }

    public FileSourceInfo readFile(UUID fileId, Long userId) {
        FileInfoDto fileInfo = getFileInfo(fileId, userId);
        if (!fileInfo.getStorageId().equals(storageId)) {
            FileStorageDto storage = kafkaRequestCreator.createRequestBuilder()
                    .withAction("file/storage")
                    .withTopicName(dbTopic)
                    .withRequestBody(fileInfo.getStorageId().toString())
                    .sendForEntity(FileServerResponseHandler.getInstance());
            String redirect = storage.getUrl().endsWith("/") ? storage.getUrl() + fileId.toString() : storage.getUrl() + "/" + fileId.toString();
            throw new RedirectException(redirect);
        }

        return new FileSourceInfo(fileStore.readFile(fileId), fileInfo);
    }

    public void deleteFile(Long userId, UUID fileId) {
        FileInfoDto fileInfo = getFileInfo(fileId, userId);
        if (!fileInfo.getStorageId().equals(storageId)) {
            FileStorageDto storage = kafkaRequestCreator.createRequestBuilder()
                    .withAction("file/storage")
                    .withTopicName(dbTopic)
                    .withRequestParameter("userId", userId)
                    .withRequestBody(fileInfo.getStorageId())
                    .sendForEntity(FileServerResponseHandler.getInstance());
            String redirect = storage.getUrl().endsWith("/") ? storage.getUrl() + fileId.toString() : storage.getUrl() + "/" + fileId.toString();
            throw new RedirectException(redirect);
        }
        var response = kafkaRequestCreator.createRequestBuilder()
                .withRequestParameters(
                        Map.of("userId", userId,
                                "fileId", fileId))
                .withAction("file/delete")
                .withTopicName(dbTopic)
                .send();





        
        FileServerResponseHandler.getInstance().handleResponse(response);
        fileStore.deleteFile(fileId);
    }
}
