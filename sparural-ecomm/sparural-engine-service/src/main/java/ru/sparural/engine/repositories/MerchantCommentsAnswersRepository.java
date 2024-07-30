package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Answer;

import java.util.List;
import java.util.Optional;

public interface MerchantCommentsAnswersRepository {
    Optional<Answer> update(String code, Long answerId, Answer answer);

    Boolean delete(String code, Long answerId);

    Optional<Answer> get(String code, Long answerId);

    Optional<Answer> create(String code, Answer answer);

    void save(Long merchantCommentId, Long optionId);

    List<Answer> getByCommentsId(Long commentsId);
}

