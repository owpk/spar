package ru.sparural.file.service.sources;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.file.FileInfoDto;
import ru.sparural.file.dto.FileSourceParameters;
import ru.sparural.file.dto.FileSourceType;
import ru.sparural.file.exceptions.ApplicationException;
import ru.sparural.file.model.FileSourceInfo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileSourceService {
    private final ApplicationContext ctx;
    private final Map<FileSourceType, FileSourceProcessor> processors = new HashMap<>();

    @PostConstruct
    public void init() {
        ctx.getBeansWithAnnotation(FileSource.class).forEach((beanName, bean) -> {
            FileSource beanAnnotation = bean.getClass().getAnnotation(FileSource.class);
            if (processors.containsKey(beanAnnotation.value())) {
                throw new ApplicationException(String.format("FileSourceProcessor '%s' already exist", beanAnnotation.value().getType()));
            }

            if (!Set.of(bean.getClass().getInterfaces()).contains(FileSourceProcessor.class)) {
                throw new ApplicationException(String.format("FileSourceProcessor of'%s' must implement FileSourceProcessor interface", beanAnnotation.value().getType()));
            }

            processors.put(beanAnnotation.value(), (FileSourceProcessor) bean);
        });
    }

    public FileSourceInfo insertFile(UUID fileId, FileInfoDto info, FileSourceType source, FileSourceParameters sourceParameters, InputStream file) {
        return processors.get(source).createFile(fileId, info, sourceParameters, file);
    }
}
