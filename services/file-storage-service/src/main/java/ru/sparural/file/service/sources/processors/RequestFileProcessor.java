package ru.sparural.file.service.sources.processors;

import lombok.AllArgsConstructor;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.file.dto.FileSourceParameters;
import ru.sparural.file.dto.FileSourceType;
import ru.sparural.file.model.FileSourceInfo;
import ru.sparural.file.service.sources.FileSource;
import ru.sparural.file.service.sources.FileSourceProcessor;

import java.io.InputStream;
import java.util.UUID;

@FileSource(FileSourceType.REQUEST)
@AllArgsConstructor
public class RequestFileProcessor implements FileSourceProcessor {

    @Override
    public FileSourceInfo createFile(UUID id, FileInfoDto info, FileSourceParameters parameters, InputStream file) {
        return new FileSourceInfo(file, info);
    }

}
