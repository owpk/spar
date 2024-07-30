package ru.sparural.file.service.sources.processors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.file.dto.FileSourceParameters;
import ru.sparural.file.dto.FileSourceType;
import ru.sparural.file.dto.sourceparams.FileSourceRemoteParams;
import ru.sparural.file.exceptions.ApplicationException;
import ru.sparural.file.model.FileSourceInfo;
import ru.sparural.file.service.sources.FileSource;
import ru.sparural.file.service.sources.FileSourceProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@FileSource(FileSourceType.REMOTE)
@Slf4j
@AllArgsConstructor
public class RemoteFileProcessor implements FileSourceProcessor {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public FileSourceInfo createFile(UUID id, FileInfoDto info, FileSourceParameters parameters, InputStream file) {
        if (!(parameters instanceof FileSourceRemoteParams)) {
            throw new ApplicationException("Incorrect source parameters type");
        }
        FileSourceRemoteParams remoteParams = (FileSourceRemoteParams) parameters;
        ResponseEntity<Resource> responseEntity = restTemplate.getForEntity(remoteParams.getUrl(), Resource.class);
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new ApplicationException("Not OK status");
        }
        try {
            Resource resource = responseEntity.getBody();
            if (resource == null) {
                throw new ApplicationException("Response have not body");
            }

            info.setMime(responseEntity.getHeaders().getContentType().getType());
            return new FileSourceInfo(resource.getInputStream(), info);
        } catch (IOException ex) {
            log.error("Error fetch file from remote addres");
            throw new ApplicationException(ex);
        }
    }

}
