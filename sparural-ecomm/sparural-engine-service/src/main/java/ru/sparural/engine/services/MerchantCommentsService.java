package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.MerchantCommentDto;
import ru.sparural.engine.api.dto.MerchantCommentsDto;
import ru.sparural.engine.entity.MerchantComments;

import java.util.List;

public interface MerchantCommentsService {
    Boolean create(MerchantCommentsDto merchantCommentsDto, Long userId);

    MerchantComments createEntity(MerchantCommentsDto dto);

    List<MerchantCommentDto> list(Integer offset,
                                  Integer limit,
                                  String search,
                                  Integer[] grade,
                                  Long dateTimeStart,
                                  Long dateTimeEnd,
                                  Long[] merchantId);

    MerchantCommentDto get(Long id);
}
