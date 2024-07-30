package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.engine.entity.Answer;
import ru.sparural.engine.repositories.MerchantCommentsAnswersRepository;
import ru.sparural.engine.repositories.MerchantCommentsQuestionsRepository;
import ru.sparural.engine.services.MerchantCommentsAnswersService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

@Service
@AllArgsConstructor
public class MerchantCommentsAnswersServiceImpl implements MerchantCommentsAnswersService {

    private final DtoMapperUtils dtoMapperUtils;
    private final MerchantCommentsAnswersRepository repository;
    private final MerchantCommentsQuestionsRepository questionsRepository;

    @Override
    public AnswerDTO createDTOFromEntity(Answer answer) {
        return dtoMapperUtils.convert(answer, AnswerDTO.class);
    }

    @Override
    public Answer createEntityFromDTO(AnswerDTO answerDTO) {
        return dtoMapperUtils.convert(answerDTO, Answer.class);
    }

    @Override
    public AnswerDTO update(String code, Long answerId, AnswerDTO answerDTO) {
        return createDTOFromEntity(repository.update(code, answerId, createEntityFromDTO(answerDTO))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public Boolean delete(String code, Long answerId) {
        repository.get(code, answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return repository.delete(code, answerId);
    }

    @Override
    public AnswerDTO create(String code, AnswerDTO answerDTO) {
        questionsRepository.get(code)
                .orElseThrow(() -> new ResourceNotFoundException("Question with this code not found"));
        return createDTOFromEntity(repository.create(code, createEntityFromDTO(answerDTO))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }
}
