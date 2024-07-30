package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Answer;
import ru.sparural.engine.entity.MerchantCommentsQuestion;
import ru.sparural.engine.entity.enums.QuestionType;
import ru.sparural.engine.repositories.MerchantCommentsQuestionsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.MerchantCommentsQuestionOptions;
import ru.sparural.tables.MerchantCommentsQuestions;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MerchantCommentsQuestionsRepositoryImpl implements MerchantCommentsQuestionsRepository {

    private final DSLContext dslContext;

    @Override
    public List<MerchantCommentsQuestion> list(Integer offset, Integer limit) {
        List<MerchantCommentsQuestion> result = dslContext
                .selectFrom(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS)
                .offset(offset)
                .limit(limit)
                .fetch().into(MerchantCommentsQuestion.class);
        return insertAnswerToBody(result);
    }

    @Override
    public List<MerchantCommentsQuestion> insertAnswerToBody(List<MerchantCommentsQuestion> result) {
        for (MerchantCommentsQuestion question : result) {
            List<Answer> res = dslContext
                    .select(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ID, MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.ANSWER)
                    .from(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS)
                    .where(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.QUESTION_ID.eq(question.getCode()))
                    .fetch().into(Answer.class);
            question.setOptions(res);
        }
        return result;
    }

    @Override
    public Boolean delete(String code) {
        dslContext
                .delete(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS)
                .where(MerchantCommentsQuestionOptions.MERCHANT_COMMENTS_QUESTION_OPTIONS.QUESTION_ID.eq(code))
                .execute();
        return dslContext
                .delete(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS)
                .where(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.CODE.eq(code))
                .execute() == 1;

    }

    @Override
    public Optional<MerchantCommentsQuestion> get(String code) {
        return dslContext
                .selectFrom(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS)
                .where(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.CODE.eq(code))
                .fetchOptionalInto(MerchantCommentsQuestion.class);
    }

    @Override
    public Optional<MerchantCommentsQuestion> update(String code, MerchantCommentsQuestion merchantCommentsQuestion) {
        return dslContext
                .update(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS)
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.QUESTION, merchantCommentsQuestion.getQuestion())
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.GRADE, merchantCommentsQuestion.getGrade())
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.UPDATED_AT, TimeHelper.currentTime())
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.TYPE, merchantCommentsQuestion.getType() != null ?
                        QuestionType.valueOf(merchantCommentsQuestion.getType()).getVal() : QuestionType.NoAnswer.getVal())
                .where(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.CODE.eq(code))
                .returning()
                .fetchOptionalInto(MerchantCommentsQuestion.class);
    }

    @Override
    public Optional<MerchantCommentsQuestion> create(MerchantCommentsQuestion merchantCommentsQuestion) {
        return dslContext
                .insertInto(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS)
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.CODE, merchantCommentsQuestion.getCode())
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.QUESTION, merchantCommentsQuestion.getQuestion())
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.GRADE, merchantCommentsQuestion.getGrade())
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.CREATED_AT, TimeHelper.currentTime())
                .set(MerchantCommentsQuestions.MERCHANT_COMMENTS_QUESTIONS.TYPE, merchantCommentsQuestion.getType() != null ?
                        QuestionType.valueOf(merchantCommentsQuestion.getType()).getVal() :
                        QuestionType.NoAnswer.getVal())
                .returning()
                .fetchOptionalInto(MerchantCommentsQuestion.class);
    }


}
