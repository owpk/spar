package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.FaqEntity;
import ru.sparural.engine.repositories.FaqRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Faq;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FaqRepositoryImpl implements FaqRepository {

    private final DSLContext dslContext;

    @Override
    public List<FaqEntity> fetch(int offset, int limit) {
        return dslContext
                .selectFrom(Faq.FAQ)
                .orderBy(Faq.FAQ.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(FaqEntity.class);
    }

    @Override
    public Optional<FaqEntity> get(Long id) {
        return dslContext
                .selectFrom(Faq.FAQ)
                .where(Faq.FAQ.ID.eq(id))
                .fetchOptionalInto(FaqEntity.class);
    }

    @Override
    public Optional<FaqEntity> update(Long id, FaqEntity faqEntity) {
        return dslContext.update(Faq.FAQ)
                .set(Faq.FAQ.QUESTION, faqEntity.getQuestion())
                .set(Faq.FAQ.ANSWER, faqEntity.getAnswer())
                .set(Faq.FAQ.ORDER, faqEntity.getOrder())
                .set(Faq.FAQ.UPDATEDAT, TimeHelper.currentTime())
                .where(Faq.FAQ.ID.eq(id))
                .returning()
                .fetchOptionalInto(FaqEntity.class);
    }

    @Override
    public Boolean delete(Long id) throws ResourceNotFoundException {
        return dslContext.delete(Faq.FAQ)
                .where(Faq.FAQ.ID.eq(id))
                .execute() == 1;
    }

    @Override
    public Optional<FaqEntity> create(FaqEntity faqEntity) {
        return dslContext.insertInto(Faq.FAQ)
                .set(Faq.FAQ.QUESTION, faqEntity.getQuestion())
                .set(Faq.FAQ.ANSWER, faqEntity.getAnswer())
                .set(Faq.FAQ.ORDER, faqEntity.getOrder())
                .set(Faq.FAQ.CREATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(FaqEntity.class);
    }
}