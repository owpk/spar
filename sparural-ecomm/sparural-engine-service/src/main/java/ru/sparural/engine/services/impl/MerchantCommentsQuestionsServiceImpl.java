package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.engine.api.dto.MerchantCommentsQuestionDTO;
import ru.sparural.engine.entity.Answer;
import ru.sparural.engine.entity.MerchantCommentsQuestion;
import ru.sparural.engine.repositories.MerchantCommentsQuestionsRepository;
import ru.sparural.engine.services.MerchantCommentsQuestionsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MerchantCommentsQuestionsServiceImpl implements MerchantCommentsQuestionsService {

    private final MerchantCommentsQuestionsRepository repository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<MerchantCommentsQuestionDTO> list(int offset, int limit) {
        return createDTOListFromEntityList(repository.list(offset, limit));
    }

    @Override
    public MerchantCommentsQuestion createEntityFromDTO(MerchantCommentsQuestionDTO merchantCommentsQuestionDTO) {
        var entity = new MerchantCommentsQuestion();
        entity.setCode(merchantCommentsQuestionDTO.getCode());
        entity.setQuestion(merchantCommentsQuestionDTO.getQuestion());
        entity.setOptions(dtoMapperUtils.convertList(Answer.class, merchantCommentsQuestionDTO.getOptions()));
        entity.setType(merchantCommentsQuestionDTO.getType());
        String grades = Arrays.stream(merchantCommentsQuestionDTO.getGrade()).mapToObj(a -> a + ",").collect(Collectors.joining());
        entity.setGrade(grades);
        return entity;
    }

    @Override
    public List<MerchantCommentsQuestionDTO> createDTOListFromEntityList(List<MerchantCommentsQuestion> merchantCommentsQuestions) {
        var dtoList = new ArrayList<MerchantCommentsQuestionDTO>();
        merchantCommentsQuestions.forEach(a -> {
            var dto = createDTOFromEntity(a);
            dtoList.add(dto);
        });
        return dtoList;
    }

    @Override
    public MerchantCommentsQuestionDTO createDTOFromEntity(MerchantCommentsQuestion merchantCommentsQuestion) {
        var dto = new MerchantCommentsQuestionDTO();
        dto.setCode(merchantCommentsQuestion.getCode());
        dto.setQuestion(merchantCommentsQuestion.getQuestion());
        if (merchantCommentsQuestion.getOptions() != null) {
            dto.setOptions(dtoMapperUtils.convertList(AnswerDTO.class, merchantCommentsQuestion.getOptions()));
        }
        dto.setType(merchantCommentsQuestion.getType());
        dto.setGrade(Arrays.stream(merchantCommentsQuestion.getGrade().split(",")).mapToInt(a -> Integer.parseInt(a)).toArray());
        return dto;
    }

    @Override
    public Boolean delete(String code) {
        repository.get(code)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return repository.delete(code);
    }

    @Override
    public MerchantCommentsQuestionDTO update(String code, MerchantCommentsQuestionDTO merchantCommentsQuestionDTO) {
        return createDTOFromEntity(repository.update(code, createEntityFromDTO(merchantCommentsQuestionDTO)).orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public MerchantCommentsQuestionDTO create(MerchantCommentsQuestionDTO merchantCommentsQuestionDTO) {
        repository.get(merchantCommentsQuestionDTO.getCode()).ifPresent(param -> {
            throw new ServiceException("Question with this code is exist");
        });
        return createDTOFromEntity(repository.create(createEntityFromDTO(merchantCommentsQuestionDTO)).orElseThrow(() -> new ServiceException("Failed to create question")));
    }


}
