package ru.sparural.file.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.sparural.engine.api.dto.file.FileDocumentDto;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.file.dto.FileSourceType;
import ru.sparural.file.exceptions.RedirectException;
import ru.sparural.file.service.FileService;
import ru.sparural.file.utils.FileServerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
public class FileRestControllerGateway {

    private final FileService fileService;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileInfoDto uploadFile(String sourceStr,
                                  String sourceParametersStr,
                                  List<FileDocumentDto> entities,
                                  MultipartFile file,
                                  String mime,
                                  String name,
                                  String ext) throws IOException {

        var source = FileSourceType.of(sourceStr);
        var sourceParameters = mapper.readValue(sourceParametersStr, source.getClassType());
        InputStream fileStream = null;
        String fileExt = null;
        String fileName = null;

        if (file != null) {
            if (StringUtils.isEmpty(mime)) {
                mime = file.getContentType();
            }

            fileStream = file.getInputStream();
            file.getOriginalFilename();
            fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
            fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
        }

        if (StringUtils.isNotEmpty(ext)) {
            fileExt = ext;
        }

        if (StringUtils.isNotEmpty(name)) {
            fileName = name;
        }
        return fileService.insertFile(source, fileName, fileExt, mime, sourceParameters, entities, fileStream);
    }

    public List<FileDocumentDto> updateFile(UUID fileId, List<FileDocumentDto> entities) throws IOException {
        Long userId = FileServerUtils.getUserId();
        return fileService.updateFile(userId, fileId, entities);
    }

    public FileInfoDto getFileInfo(UUID fileId) {
        Long userId = FileServerUtils.getUserId();
        return fileService.getFileInfo(fileId, userId);
    }

    public void getFile(UUID fileId, HttpServletResponse response) throws IOException {
        Long userId = FileServerUtils.getUserId();
        try {
            var fileSourceInfo = fileService.readFile(fileId, userId);
            response.setContentType(fileSourceInfo.getFileInfo().getMime());
            response.setHeader("Content-Disposition",
                    String.format(
                            "form-data; name=\"%s\" filename=\"%s\"",
                            fileSourceInfo.getFileInfo().getName(),
                            fileSourceInfo.getFileInfo().getName() + "." + fileSourceInfo.getFileInfo().getExt()
                    )
            );
            try (InputStream is = fileSourceInfo.getFileInputStream()) {
                is.transferTo(response.getOutputStream());
            }
            response.getOutputStream().flush();
        } catch (RedirectException rex) {
            response.sendRedirect(rex.getRedirectUrl());
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    public void deleteFile(UUID fileId) {
        Long userId = FileServerUtils.getUserId();
        fileService.deleteFile(userId, fileId);
    }
}
