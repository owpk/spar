package ru.sparural.file.service.store;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.sparural.file.exceptions.ApplicationException;
import ru.sparural.file.exceptions.FileDoesNotExistException;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@NoArgsConstructor
@Slf4j
public class DiskFileStore implements FileStore {

    @Value("${storage.path}")
    private String path;

    @Value("${storage.temp}")
    private String temp;

    @Override
    public Long writeFileToTemporary(UUID id, InputStream fileStream) {
        File fd = new File(temp + "/" + id.toString());
        try {
            if (!fd.exists()) {
                fd.createNewFile();
            }
            try (OutputStream of = new FileOutputStream(fd)) {
                return fileStream.transferTo(of);
            }
        } catch (IOException ex) {
            log.error("Error to write file", ex);
            throw new ApplicationException(ex);
        }
    }

    @Override
    public InputStream readFile(UUID id) {
        File fd = new File(path + "/" + id.toString());
        if (!fd.exists()) {
            throw new FileDoesNotExistException();
        }

        try {
            return new FileInputStream(fd);
        } catch (FileNotFoundException ex) {
            throw new FileDoesNotExistException();
        }
    }

    @Override
    public void deleteTemporaryFile(UUID id) {
        File fd = new File(temp + "/" + id.toString());
        fd.delete();
    }

    @Override
    public void moveFileFromTemporaryToStore(UUID id) {
        File srcFd = new File(temp + "/" + id.toString());
        File dstFd = new File(path + "/" + id.toString());
        try {
            Files.copy(srcFd.toPath(), dstFd.toPath());
        } catch (IOException ex) {
           log.error("Error on move temporary file to store", ex);
        } finally {
           srcFd.delete();
        }

    }

    @Override
    public void deleteFile(UUID id) {
        File fd = new File(path + "/" + id.toString());
        fd.delete();
    }

}
