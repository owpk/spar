package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MerchantCommentsQuestionOption;

import java.util.Optional;

public interface MerchantCommentQuestionOptionsRepository {
    Optional<MerchantCommentsQuestionOption> findByIdAndCodeOfQuestion(Long id, String questionId);
}
