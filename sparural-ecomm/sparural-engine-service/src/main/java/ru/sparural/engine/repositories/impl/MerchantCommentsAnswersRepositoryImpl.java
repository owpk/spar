package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Answer;
import ru.sparural.engine.repositories.MerchantCommentsAnswersRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.MerchantCommentAnswers;
import ru.sparural.tables.MerchantCommentsQuestionOptions;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantCommentsAnswersRepositoryImpl implements MerchantCommentsAnswersRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<Answer> update(String code, Long answerId, Answer answer) {
        return dslContext
                .update(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS)
                .set(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ANSWER, answer.getAnswer())
                .set(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.UPDATED_AT, TimeHelper.currentTime())
                .where(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.QUESTION_ID.eq(code)
                        .and(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID.eq(answerId)))
                .returning()
                .fetchOptionalInto(Answer.class);
    }

    @Override
    public Boolean delete(String code, Long answerId) {
        return dslContext
                .delete(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS)
                .where(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.QUESTION_ID.eq(code)
                        .and(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID.eq(answerId)))
                .execute() == 1;

    }

    @Override
    public Optional<Answer> get(String code, Long answerId) {
        return dslContext
                .select(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID, MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ANSWER)
                .from(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS)
                .where(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.QUESTION_ID.eq(code)
                        .and(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID.eq(answerId)))
                .fetchOptionalInto(Answer.class);
    }

    @Override
    public Optional<Answer> create(String code, Answer answer) {
        return dslContext
                .insertInto(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS)
                .set(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.QUESTION_ID, code)
                .set(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ANSWER, answer.getAnswer())
                .set(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.CREATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Answer.class);

    }

    @Override
    public void save(Long merchantCommentId, Long optionId) {
        dslContext
                .insertInto(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS)
                .set(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.MERCHANT_COMMENT_ID, merchantCommentId)
                .set(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.OPTION_ID, optionId)
                .set(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.CREATED_AT, TimeHelper.currentTime())
                .set(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.UPDATED_AT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public List<Answer> getByCommentsId(Long commentsId) {
        return dslContext.select()
                .from(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS
                        .leftJoin(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS)
                        .on(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID
                                .eq(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.OPTION_ID)))
                .where(MerchantCommentAnswers.MERCHANT_COMMENT_ANSWERS.MERCHANT_COMMENT_ID
                        .eq(commentsId))
                .fetchInto(Answer.class);
    }


}
