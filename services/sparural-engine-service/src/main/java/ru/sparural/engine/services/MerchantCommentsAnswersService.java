package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.engine.entity.Answer;

public interface MerchantCommentsAnswersService {

    AnswerDTO createDTOFromEntity(Answer answer);

    Answer createEntityFromDTO(AnswerDTO answerDTO);

    AnswerDTO update(String code, Long answerId, AnswerDTO answerDTO);

    Boolean delete(String code, Long answerId);

    AnswerDTO create(String code, AnswerDTO answerDTO);
}
