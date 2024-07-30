package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.file.FileDocumentDto;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.api.enums.FileDocumentTypes;
import ru.sparural.engine.entity.Role;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.entity.file.FileDocumentEntity;
import ru.sparural.engine.entity.file.FileEntity;
import ru.sparural.engine.entity.file.FileStorageEntity;
import ru.sparural.engine.entity.file.FileWithStorageEntity;
import ru.sparural.engine.repositories.FileDocumentRepository;
import ru.sparural.engine.repositories.UserRepository;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.exception.FileStorageIsFull;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.UnauthorizedException;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.gobals.RoleNames;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FileDocumentServiceImpl implements FileDocumentService {

    private final FileDocumentRepository fileDocumentRepository;
    private final DtoMapperUtils dtoMapperUtils;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FileInfoDto createFile(FileInfoDto fileInfo, Long userId) {
        FileStorageEntity storage = fileDocumentRepository.findFileStorageById(fileInfo.getStorageId());
        if ((storage.getUsed() + fileInfo.getSize()) > storage.getSize()) {
            throw new FileStorageIsFull();
        }

        //If userId == -1, then this system operation. Set admin role for it
        User user = userRepository.get(userId).orElseGet(() -> {
            if (userId >= 0L) {
                throw new ResourceNotFoundException(String.format("User with id %d does not exist", userId));
            }
            var result = new User();
            var adminRole = new Role();
            adminRole.setCode(RoleNames.ADMIN.getName());
            result.setId(userId);
            result.setRoles(Collections.singletonList(adminRole));
            return result;
        });

        FileEntity fileEntity = dtoMapperUtils.convert(fileInfo, FileEntity.class);
        List<FileDocumentEntity> documentEntities = dtoMapperUtils.convertList(FileDocumentEntity.class, fileInfo.getEntities());

        fileDocumentRepository.updateFileStorageSizeById(fileInfo.getStorageId(), fileInfo.getSize());
        fileEntity = fileDocumentRepository.createFile(fileEntity);
        documentEntities = documentEntities.stream()
                .map(document -> linkDocumentToFile(document, user))
                .collect(Collectors.toList());
        FileInfoDto result = dtoMapperUtils.convert(fileEntity, FileInfoDto.class);
        result.setEntities(dtoMapperUtils.convertList(FileDocumentDto.class, documentEntities));
        return result;
    }

    private FileDocumentEntity linkDocumentToFile(FileDocumentEntity document, User user) {
        FileDocumentTypes documentType = document.getField().getDocumentType();
        if (!documentType.isWriteProtected()) {
            Set<Long> documentOwners = fileDocumentRepository.getDocumentOwner(documentType, document.getDocumentId());
            if (!documentOwners.contains(user.getId())) {
                throw new UnauthorizedException(String.format("User %d have is not owner for document %s:%d",
                        user.getId(), documentType.getName(), document.getId()));
            }
        } else {
            if (!fileDocumentRepository.isDocumentExist(documentType, document.getId())) {
                throw new ResourceNotFoundException(String.format("Document %s with id %d does not exist", documentType.getName(), document.getId()));
            }

            user.getRoles().stream()
                    .filter(role -> StringUtils.hasText(role.getCode()))
                    .filter(role -> role.getCode().equals(RoleNames.ADMIN.getName()) || role.getCode().equals(RoleNames.MANAGER.getName()))
                    .findFirst()
                    .orElseThrow(() -> new UnauthorizedException("Only admin or manager can create files for public documents"));
        }

        if (document.getField().isMulty()) {
            return fileDocumentRepository.insertFileDocument(document);
        }

        List<FileDocumentEntity> docs = fileDocumentRepository.findFileDocumentsByIdAndType(document.getId(), document.getField());
        if (docs.isEmpty()) {
            return fileDocumentRepository.insertFileDocument(document);
        }

        return fileDocumentRepository.updateFileDocument(docs.get(0).getId(), document);
    }

    private void unlinkDocumentToFile(FileDocumentEntity document, User user) {
        FileDocumentTypes documentType = document.getField().getDocumentType();
        if (!documentType.isWriteProtected()) {
            Set<Long> documentOwners = fileDocumentRepository.getDocumentOwner(documentType, document.getDocumentId());
            if (!documentOwners.contains(user.getId())) {
                throw new UnauthorizedException(String.format("User %d have is not owner for document %s:%d",
                        user.getId(), documentType.getName(), document.getId()));
            }
        } else {
            user.getRoles().stream()
                    .filter(role -> StringUtils.hasText(role.getCode()))
                    .filter(role -> role.getCode().equals(RoleNames.ADMIN.getName()) || role.getCode().equals(RoleNames.MANAGER.getName()))
                    .findFirst()
                    .orElseThrow(() -> new UnauthorizedException("Only admin or manager can change files for public documents"));
        }

        fileDocumentRepository.removeFileDocument(document.getId());
    }

    @Override
    public FileEntity getFile(UUID fileId) {
        return fileDocumentRepository.findFileById(fileId);
    }

    @Override
    public List<FileDocumentEntity> getFileDocumentsForFile(UUID fileId, Long userId) {
        User user = userId != null ? userRepository.get(userId).orElse(null) : null;
        List<FileDocumentEntity> result = fileDocumentRepository.findFileDocumentsByFileId(fileId);
        result.forEach(doc -> {
            FileDocumentTypes docType = doc.getField().getDocumentType();
            if (docType.isReadProtected()) {
                if (user == null) {
                    throw new UnauthorizedException("You have not permissions to this document");
                }
                Set<Long> documentOwners = fileDocumentRepository.getDocumentOwner(docType, doc.getDocumentId());
                if (!documentOwners.contains(user.getId())) {
                    user.getRoles().stream()
                            .filter(role -> RoleNames.ADMIN.getName().equals(role.getCode()))
                            .findFirst()
                            .orElseThrow(() -> new UnauthorizedException("You have not permissions to this document"));
                }
            }
        });
        return result;
    }

    @Override
    public FileStorageEntity getFileStorage(UUID fileStorageId) {
        return fileDocumentRepository.findFileStorageById(fileStorageId);
    }

    @Override
    public List<FileDto> getFileByDocumentId(FileDocumentTypeField field, Long documentId) {
        return fileDocumentRepository.findFileWithStorageByDocumentField(field, documentId).stream()
                .map(this::mapFileWithStorageEntityToFileDto)
                .collect(Collectors.toList());
    }

    private FileDto mapFileWithStorageEntityToFileDto(FileWithStorageEntity entity) {
        FileDto dto = new FileDto();
        dto.setExt(entity.getFile().getExt());
        dto.setMime(entity.getFile().getMime());
        dto.setSize(entity.getFile().getSize());

        String url = entity.getStorage().getUrl();
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        url = url + entity.getFile().getId().toString();
        dto.setUrl(url);

        dto.setUuid(entity.getFile().getId().toString());
        dto.setName(entity.getFile().getName());
        return dto;
    }

    @Override
    @Transactional
    public List<FileDocumentEntity> updateFileDocuments(Long userId, UUID fileId, List<FileDocumentEntity> documents) {
        fileDocumentRepository.findFileById(fileId);
        documents.forEach(document -> document.setFileId(fileId));
        User user = userRepository.get(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d does not exist", userId)));
        List<FileDocumentEntity> oldEntities = fileDocumentRepository.findFileDocumentsByFileId(fileId);

        Set<FileDocumentEntity> entitiesSet = new TreeSet<>(this::compareDocumentsByDocument);
        entitiesSet.addAll(documents);
        oldEntities.forEach(entitiesSet::remove);
        entitiesSet.forEach(document -> linkDocumentToFile(document, user));

        entitiesSet = new TreeSet<>(this::compareDocumentsByDocument);
        entitiesSet.addAll(oldEntities);
        documents.forEach(entitiesSet::remove);
        entitiesSet.forEach(document -> {
            unlinkDocumentToFile(document, user);
        });

        return getFileDocumentsForFile(fileId, userId);
    }


    private int compareDocumentsByDocument(FileDocumentEntity doc1, FileDocumentEntity doc2) {
        int res = doc1.getDocumentId().compareTo(doc2.getDocumentId());
        if (res == 0) {
            res = doc1.getField().name().compareTo(doc2.getField().name());
        }
        return res;
    }

    @Override
    @Transactional
    public void deleteFile(Long userId, UUID fileId) {
        User user = userRepository.get(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d does not exist", userId)));
        fileDocumentRepository.findFileDocumentsByFileId(fileId).forEach(document -> {
            unlinkDocumentToFile(document, user);
        });

        fileDocumentRepository.removeFileById(fileId);
    }
}
