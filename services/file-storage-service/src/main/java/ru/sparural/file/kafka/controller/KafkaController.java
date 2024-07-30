package ru.sparural.file.kafka.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.sparural.engine.api.dto.file.FileDocumentDto;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.engine.api.dto.file.FileUploadRequest;
import ru.sparural.file.dto.FileSourceParameters;
import ru.sparural.file.dto.FileSourceType;
import ru.sparural.file.exceptions.ApplicationException;
import ru.sparural.file.service.FileService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.file}")
@AllArgsConstructor
public class KafkaController {
    private final FileService fileService;
    private final ModelMapper mapper = new ModelMapper();

    @KafkaSparuralMapping("file/documents/update")
    public List<FileDocumentDto> updateFile(@RequestParam Long userId, @RequestParam String fileIdString, @Payload List<FileDocumentDto> documents) {

        UUID fileId = UUID.fromString(fileIdString);

        return fileService.updateFile(userId, fileId, documents);
    }

    @KafkaSparuralMapping("file/delete")
    public void deleteFile(@RequestParam Long userId, @RequestParam String fileIdString) {
        UUID fileId = UUID.fromString(fileIdString);
        fileService.deleteFile(userId, fileId);
    }

    @KafkaSparuralMapping("file/upload")
    public FileInfoDto uploadFile(@RequestParam Long userId, @Payload FileUploadRequest request) throws IOException {
        FileSourceType source = FileSourceType.of(request.getSource());
        if (FileSourceType.REQUEST.equals(source)) {
            throw new ApplicationException("Does not allow send files by Kafka");
        }
        FileSourceParameters sourceParameters = mapper.map(request.getSourceParameters(), source.getClassType());

        return fileService.insertFile(source,
                request.getExt(),
                request.getName(),
                request.getMime(),
                sourceParameters,
                request.getEntities(),
                null
        );
    }

    @KafkaSparuralMapping("file/info")
    public FileInfoDto getFileInfo(@RequestParam Long userId, @RequestParam("fileId") String fileIdStr) {
        UUID fileId = UUID.fromString(fileIdStr);
        return fileService.getFileInfo(fileId, userId);
    }
}
