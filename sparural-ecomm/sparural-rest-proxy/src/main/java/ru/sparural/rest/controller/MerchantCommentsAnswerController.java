package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.MerchantCommentsAnswersCacheService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/merchant-comments-answers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "merchants")
public class MerchantCommentsAnswerController {

    private final MerchantCommentsAnswersCacheService merchantCommentsAnswersCacheService;

    @IsManagerOrAdmin
    @PutMapping("/{code}/answers/{answerId}")
    public DataResponse<AnswerDTO> update(
            @PathVariable String code,
            @PathVariable Long answerId,
            @Valid @RequestBody DataRequest<AnswerDTO> restRequest) {
        return merchantCommentsAnswersCacheService.update(code, answerId, restRequest.getData());
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{code}/answers/{answerId}")
    public DataResponse<Boolean> delete(@PathVariable String code,
                                        @PathVariable Long answerId) {
        return merchantCommentsAnswersCacheService.delete(code, answerId);
    }

    @IsManagerOrAdmin
    @PostMapping("/{code}/answers")
    public DataResponse<AnswerDTO> create(@PathVariable String code,
                                          @Valid @RequestBody DataRequest<AnswerDTO> answerDTO) {
        return merchantCommentsAnswersCacheService.create(code, answerDTO.getData());
    }
}
