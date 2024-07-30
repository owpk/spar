package ru.sparural.rest.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.engine.api.dto.MerchantCommentsQuestionDTO;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.MerchantCommentsAnswersCacheService;
import ru.sparural.rest.services.MerchantCommentsQuestionsCacheService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/merchant-comments-questions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "merchants")
public class MerchantCommentsQuestionsController {

    private final MerchantCommentsQuestionsCacheService merchantCommentsQuestionsCacheService;
    private final MerchantCommentsAnswersCacheService merchantCommentsAnswersCacheService;

    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    @GetMapping
    public DataResponse<List<MerchantCommentsQuestionDTO>> list(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "30") Integer limit) {
        return merchantCommentsQuestionsCacheService.list(offset, limit);
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{code}")
    public DataResponse<Boolean> delete(@PathVariable String code) {
        return merchantCommentsQuestionsCacheService.delete(code);
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{code}/answers/{answerId}")
    public DataResponse<Boolean> delete(@PathVariable String code,
                                        @PathVariable Long answerId) {
        return merchantCommentsAnswersCacheService.delete(code, answerId);
    }

    @IsManagerOrAdmin
    @PutMapping("/{code}")
    public DataResponse<MerchantCommentsQuestionDTO> update(@PathVariable String code,
                                                            @Valid @RequestBody DataRequest<MerchantCommentsQuestionDTO> merchantCommentsQuestionDTO) {
        return merchantCommentsQuestionsCacheService.update(code, merchantCommentsQuestionDTO.getData());
    }

    @IsManagerOrAdmin
    @PostMapping
    public DataResponse<MerchantCommentsQuestionDTO> create(@Valid @RequestBody DataRequest<MerchantCommentsQuestionDTO> merchantCommentsQuestionDTO) {
        //MerchantCommentsQuestionDTO merchantCommentsQuestion = new MerchantCommentsQuestionDTO();
        //BeanUtils.copyProperties(merchantCommentsQuestionDTO.getData(), merchantCommentsQuestion);
        return merchantCommentsQuestionsCacheService.create(merchantCommentsQuestionDTO.getData());
    }

    @IsManagerOrAdmin
    @PostMapping("/{code}/options")
    public DataResponse<AnswerDTO> create(@PathVariable String code,
                                          @Valid @RequestBody DataRequest<AnswerDTO> answerDTO) {
        return merchantCommentsAnswersCacheService.create(code, answerDTO.getData());
    }
}

