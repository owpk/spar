package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MerchantCommentsQuestion;

import java.util.List;
import java.util.Optional;

public interface MerchantCommentsQuestionsRepository {
    List<MerchantCommentsQuestion> list(Integer offset, Integer limit);

    List<MerchantCommentsQuestion> insertAnswerToBody(List<MerchantCommentsQuestion> result);

    Boolean delete(String code);

    Optional<MerchantCommentsQuestion> get(String code);

    Optional<MerchantCommentsQuestion> update(String code, MerchantCommentsQuestion merchantCommentsQuestion);

    Optional<MerchantCommentsQuestion> create(MerchantCommentsQuestion merchantCommentsQuestion);

}
