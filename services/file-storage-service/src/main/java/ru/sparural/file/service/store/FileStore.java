package ru.sparural.file.service.store;

import java.io.InputStream;
import java.util.UUID;

public interface FileStore {

    Long writeFileToTemporary(UUID id, InputStream fileStream);

    void moveFileFromTemporaryToStore(UUID id);

    InputStream readFile(UUID id);

    void deleteFile(UUID id);

    void deleteTemporaryFile(UUID id);
}
