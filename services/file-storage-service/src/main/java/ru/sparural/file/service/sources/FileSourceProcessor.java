package ru.sparural.file.service.sources;

import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.file.dto.FileSourceParameters;
import ru.sparural.file.model.FileSourceInfo;

import java.io.InputStream;
import java.util.UUID;

public interface FileSourceProcessor {

    FileSourceInfo createFile(UUID id, FileInfoDto info, FileSourceParameters parameters, InputStream file);
}
