package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.MerchantCommentsQuestionDTO;
import ru.sparural.engine.entity.MerchantCommentsQuestion;

import java.util.List;

public interface MerchantCommentsQuestionsService {

    List<MerchantCommentsQuestionDTO> list(int offset, int limit);

    MerchantCommentsQuestion createEntityFromDTO(MerchantCommentsQuestionDTO merchantCommentsQuestionDTO);

    List<MerchantCommentsQuestionDTO> createDTOListFromEntityList(List<MerchantCommentsQuestion> merchantCommentsQuestions);

    MerchantCommentsQuestionDTO createDTOFromEntity(MerchantCommentsQuestion merchantCommentsQuestion);

    Boolean delete(String code);

    MerchantCommentsQuestionDTO update(String code, MerchantCommentsQuestionDTO merchantCommentsQuestionDTO);

    MerchantCommentsQuestionDTO create(MerchantCommentsQuestionDTO merchantCommentsQuestionDTO);


}
