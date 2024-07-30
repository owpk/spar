package ru.sparural.file.rest.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sparural.engine.api.dto.file.FileDocumentDto;
import ru.sparural.engine.api.dto.file.FileInfoDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class FileRestController {

    private final FileRestControllerGateway fileRestControllerGateway;

    @PreAuthorize("!hasAuthority('ROLE_ANONYMOUS')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public FileInfoDto uploadFile(@RequestPart("source") String sourceStr,
                                  @RequestPart("source-parameters") String sourceParametersStr,
                                  @RequestPart("entities") List<FileDocumentDto> entities,
                                  @RequestPart(value = "file", required = false) MultipartFile file,
                                  @RequestPart(value = "mime", required = false) String mime,
                                  @RequestPart(value = "name", required = false) String name,
                                  @RequestPart(value = "ext", required = false) String ext) throws IOException {

        return fileRestControllerGateway.uploadFile(sourceStr, sourceParametersStr, entities, file, mime, name, ext);
    }

    @PreAuthorize("!hasAuthority('ROLE_ANONYMOUS')")
    @PutMapping("/{uuid}/documents")
    public List<FileDocumentDto> updateFile(@PathVariable("uuid") UUID fileId, @RequestBody List<FileDocumentDto> entities) throws IOException {
        return fileRestControllerGateway.updateFile(fileId, entities);
    }

    @GetMapping(value = "/{uuid}/info", produces = {MediaType.APPLICATION_JSON_VALUE})
    public FileInfoDto getFileInfo(@PathVariable("uuid") UUID fileId) {
        return fileRestControllerGateway.getFileInfo(fileId);
    }

    @GetMapping(value = "/{uuid}")
    public void getFile(@PathVariable("uuid") UUID fileId, HttpServletResponse response) throws IOException {
        fileRestControllerGateway.getFile(fileId, response);
    }

    @DeleteMapping(value = "/{uuid}")
    @PreAuthorize("!hasAuthority('ROLE_ANONYMOUS')")
    public void deleteFile(@PathVariable("uuid") UUID fileId) {
        fileRestControllerGateway.deleteFile(fileId);
    }
}
