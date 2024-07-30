package ru.sparural.engine.gateways;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.file.FileDocumentDto;
import ru.sparural.engine.api.dto.file.FileUploadRequest;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.config.KafkaTopics;
import ru.sparural.file.dto.sourceparams.FileSourceRemoteParams;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileServiceGateway {

    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final KafkaTopics kafkaTopics;

    @Getter
    @Setter
    @Value("${sparural.gateways.fileservice.thread.count:10}")
    private int threadCount;

    @Getter
    @Setter
    private String fileServiceTopic;

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        fileServiceTopic = kafkaTopics.getFileRequestTopicName();
    }

    public CompletableFuture<FileDto> uploadFileFromUrlAsync(String url, FileDocumentTypeField documentField, Long documentId) {
        CompletableFuture<FileDto> future = new CompletableFuture<>();
        threadPoolExecutor.submit(() -> {
            var futureResult = this.uploadFileFromUrl(url, documentField, documentId);
            future.complete(futureResult);
        });
        return future;
    }

    public FileDto uploadFileFromUrl(String url, FileDocumentTypeField documentField, Long documentId) {
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setSource("remote");
        fileUploadRequest.setSourceParameters(new FileSourceRemoteParams(url));
        fileUploadRequest.setEntities(Collections.singletonList(new FileDocumentDto(null, documentField, documentId)));

        return kafkaRequestCreator
                .createRequestBuilder()
                .withRequestBody(fileUploadRequest)
                .withTopicName("lp.file.request")
                .withAction("file/upload")
                .withRequestParameter("userId", -1L)
                .sendForEntity();
    }
}
