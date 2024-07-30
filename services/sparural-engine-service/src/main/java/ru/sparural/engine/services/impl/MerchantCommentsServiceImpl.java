package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.engine.api.dto.MerchantCommentDto;
import ru.sparural.engine.api.dto.MerchantCommentsDto;
import ru.sparural.engine.entity.MerchantComment;
import ru.sparural.engine.entity.MerchantComments;
import ru.sparural.engine.repositories.MerchantCommentQuestionOptionsRepository;
import ru.sparural.engine.repositories.MerchantCommentsAnswersRepository;
import ru.sparural.engine.repositories.MerchantCommentsQuestionsRepository;
import ru.sparural.engine.repositories.MerchantCommentsRepository;
import ru.sparural.engine.repositories.MerchantRepository;
import ru.sparural.engine.services.CardsService;
import ru.sparural.engine.services.MerchantCommentsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantCommentsServiceImpl implements MerchantCommentsService {
    private final MerchantCommentsRepository repository;
    private final DtoMapperUtils dtoMapperUtils;
    private final CardsService cardsService;
    private final MerchantRepository merchantRepository;
    private final MerchantCommentsQuestionsRepository merchantCommentsQuestionsRepository;
    private final MerchantCommentQuestionOptionsRepository merchantCommentQuestionOptionsRepository;
    private final MerchantCommentsAnswersRepository merchantCommentsAnswersRepository;

    @Override
    public Boolean create(MerchantCommentsDto merchantCommentsDto, Long userId) {
        merchantRepository.findById(merchantCommentsDto.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("This merchant not exist"));


        if (merchantCommentsDto.getQuestions() != null) {
            merchantCommentsDto.getQuestions().forEach(a -> {
                merchantCommentsQuestionsRepository.get(a.getQuestionsId())
                        .orElseThrow(() -> new ResourceNotFoundException("This question not exist"));
                merchantCommentQuestionOptionsRepository.findByIdAndCodeOfQuestion(a.getOptionId(), a.getQuestionsId())
                        .orElseThrow(() -> new ResourceNotFoundException("This option not exist"));

            });
        }

        var cardId = cardsService.findCardIdByUserId(userId);
        var optMerchantsComment = repository.create(createEntity(merchantCommentsDto), userId, cardId);
        if (optMerchantsComment.isPresent() && merchantCommentsDto.getQuestions() != null) {
            merchantCommentsDto.getQuestions().forEach(a ->
                    merchantCommentsAnswersRepository.save(optMerchantsComment.get().getId(), a.getOptionId())
            );
        }
        return optMerchantsComment.isPresent();
    }

    @Override
    public MerchantComments createEntity(MerchantCommentsDto dto) {
        return dtoMapperUtils.convert(MerchantComments.class, () -> dto);
    }

    @Override
    public List<MerchantCommentDto> list(Integer offset,
                                         Integer limit,
                                         String search,
                                         Integer[] grade,
                                         Long dateTimeStart,
                                         Long dateTimeEnd,
                                         Long[] merchantId) {
        var list = createListDto(repository.list(offset,
                limit,
                search,
                grade,
                dateTimeStart,
                dateTimeEnd,
                merchantId));
        list.forEach(a -> {
            var options = dtoMapperUtils.convertList(AnswerDTO.class, a.getOptions());
            if (options.size() != 0) {
                a.setOptions(options);
            }
        });
        return list;
    }

    @Override
    public MerchantCommentDto get(Long id) {
        return dtoMapperUtils.convert(repository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Отзыв не найден")), MerchantCommentDto.class);
    }

    public List<MerchantCommentDto> createListDto(List<MerchantComment> entityList) {
        return dtoMapperUtils.convertList(MerchantCommentDto.class, () -> entityList);
    }
}
