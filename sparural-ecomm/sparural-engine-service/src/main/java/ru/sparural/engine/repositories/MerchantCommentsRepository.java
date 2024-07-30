package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MerchantComment;
import ru.sparural.engine.entity.MerchantComments;

import java.util.List;
import java.util.Optional;

public interface MerchantCommentsRepository {
    Optional<MerchantComments> create(MerchantComments entity, Long userId, Long cardId);

    List<MerchantComment> list(Integer offset,
                               Integer limit,
                               String search,
                               Integer[] grade,
                               Long dateTimeStart,
                               Long dateTimeEnd,
                               Long[] merchantId);

    Optional<MerchantComment> get(Long id);
}
