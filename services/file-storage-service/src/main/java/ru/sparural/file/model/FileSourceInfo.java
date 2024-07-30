package ru.sparural.file.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sparural.engine.api.dto.file.FileInfoDto;

import java.io.InputStream;

@Data
@AllArgsConstructor
public class FileSourceInfo {
    private InputStream fileInputStream;
    FileInfoDto fileInfo;
}
