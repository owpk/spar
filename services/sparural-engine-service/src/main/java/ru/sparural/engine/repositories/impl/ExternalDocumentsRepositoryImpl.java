package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.ExternalDocument;
import ru.sparural.engine.repositories.ExternalDocumentsRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.ExternalDocuments;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalDocumentsRepositoryImpl implements ExternalDocumentsRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<ExternalDocument> create(ExternalDocument data) {
        return dslContext.insertInto(ExternalDocuments.EXTERNAL_DOCUMENTS)
                .set(ExternalDocuments.EXTERNAL_DOCUMENTS.ALIAS, data.getAlias())
                .set(ExternalDocuments.EXTERNAL_DOCUMENTS.TITLE, data.getTitle())
                .set(ExternalDocuments.EXTERNAL_DOCUMENTS.URL, data.getUrl())
                .set(ExternalDocuments.EXTERNAL_DOCUMENTS.CREATEDAT, TimeHelper.currentTime())
                .onConflict(ExternalDocuments.EXTERNAL_DOCUMENTS.ALIAS)
                .doNothing()
                .returning()
                .fetchOptionalInto(ExternalDocument.class);
    }

    @Override
    public boolean deleteByAlias(String alias) {
        return dslContext.delete(ExternalDocuments.EXTERNAL_DOCUMENTS).where(ExternalDocuments.EXTERNAL_DOCUMENTS.ALIAS.eq(alias)).execute() == 1;
    }

    @Override
    public Optional<ExternalDocument> updateByAlias(String alias, ExternalDocument data) {
        try {
            return dslContext.update(ExternalDocuments.EXTERNAL_DOCUMENTS)
                    .set(ExternalDocuments.EXTERNAL_DOCUMENTS.ALIAS, data.getAlias())
                    .set(ExternalDocuments.EXTERNAL_DOCUMENTS.TITLE, data.getTitle())
                    .set(ExternalDocuments.EXTERNAL_DOCUMENTS.URL, data.getUrl())
                    .set(ExternalDocuments.EXTERNAL_DOCUMENTS.UPDATEDAT, TimeHelper.currentTime())
                    .where(ExternalDocuments.EXTERNAL_DOCUMENTS.ALIAS.eq(alias))
                    .returning()
                    .fetchOptionalInto(ExternalDocument.class);
        } catch (DuplicateKeyException e) {
            throw new ResourceNotFoundException("Alias exists");
        }
    }

    @Override
    public Optional<ExternalDocument> getByAlias(String alias) throws ResourceNotFoundException {
        return dslContext
                .selectFrom(ExternalDocuments.EXTERNAL_DOCUMENTS)
                .where(ExternalDocuments.EXTERNAL_DOCUMENTS.ALIAS.eq(alias))
                .fetchOptionalInto(ExternalDocument.class);
    }

    @Override
    public List<ExternalDocument> getList(int offset, int limit) {
        return dslContext.selectFrom(ExternalDocuments.EXTERNAL_DOCUMENTS)
                .orderBy(ExternalDocuments.EXTERNAL_DOCUMENTS.ID.desc())
                .offset(offset).limit(limit).fetch().into(ExternalDocument.class);
    }

}

