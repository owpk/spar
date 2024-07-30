package ru.sparural.engine.entity.file;

import lombok.Data;

@Data
public class FileWithStorageEntity {
    private FileEntity file;
    private FileStorageEntity storage;
}
