package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.MerchantCommentsQuestionOption;
import ru.sparural.engine.repositories.MerchantCommentQuestionOptionsRepository;
import ru.sparural.tables.MerchantCommentsQuestionOptions;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantCommentQuestionOptionsRepositoryImpl implements MerchantCommentQuestionOptionsRepository {
    private final DSLContext dslContext;
    private final MerchantCommentsQuestionOptions table = MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS;

    @Override
    public Optional<MerchantCommentsQuestionOption> findByIdAndCodeOfQuestion(Long id, String questionId) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .and(table.QUESTIONID.eq(questionId))
                .fetchOptionalInto(MerchantCommentsQuestionOption.class);
    }
}
