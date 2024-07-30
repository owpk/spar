package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.CodeDto;
import ru.sparural.engine.api.dto.cards.*;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/cards", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "cards")
public class CardController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @GetMapping("/select")
    public DataResponse<UserCardsAccountsDto> select(@ApiIgnore UserPrincipal userPrincipal) {
        UserCardsAccountsDto success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/select")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<UserCardsAccountsDto>builder()
                .data(success)
                .success(true)
                .build();
    }

    @IsClient
    @PostMapping("/attach")
    public UnwrappedGenericDto<EmptyObject> attach(@Valid @RequestBody CardNumberPasswordRequestDto userCardDto,
                                                   @ApiIgnore UserPrincipal userPrincipal) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withRequestBody(userCardDto)
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/attach")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(success)
                .build();
    }

    @IsClient
    @PostMapping
    public DataResponse<UserCardDto> create(@Valid @RequestBody CardNumberRequestDto cardNumberRequestDto,
                                            @ApiIgnore UserPrincipal userPrincipal) {
        UserCardDto cardDto = restToKafkaService.createRequestBuilder()
                .withRequestBody(cardNumberRequestDto)
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/create")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<UserCardDto>builder()
                .data(cardDto)
                .build();
    }

    @IsClient
    @PostMapping("/emit-virtual")
    public DataResponse<UserCardDto> emitVirtual(@ApiIgnore UserPrincipal userPrincipal) {
        UserCardDto cardDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/emit-virtual")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<UserCardDto>builder()
                .success(true)
                .data(cardDto)
                .version(1)
                .build();
    }

    @IsClient
    @PostMapping("/attach-confirm")
    public DataResponse<UserCardDto> attachConfirm(@ApiIgnore UserPrincipal userPrincipal,
                                                   @Valid @RequestBody CodeDto codeDto) {
        UserCardDto cardDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/attach-confirm")
                .withRequestBody(codeDto)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<UserCardDto>builder()
                .data(cardDto)
                .build();
    }

    @PostMapping("/attach-send-confirm-code")
    public UnwrappedGenericDto<EmptyObject> sendConfirmAttachCode(@ApiIgnore UserPrincipal userPrincipal) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/attach-send-confirm-code")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(success)
                .build();
    }

    @IsClient
    @PostMapping("/{id}/change-block-state")
    public DataResponse<UserCardDto> changeBlockState(@Valid @RequestBody CardPasswordDto cardPasswordDto,
                                                      @PathVariable Long id,
                                                      @ApiIgnore UserPrincipal userPrincipal) {
        UserCardDto cardDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/change-block-state")
                .withRequestBody(cardPasswordDto)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<UserCardDto>builder()
                .data(cardDto)
                .build();
    }

    @IsClient
    @PostMapping("{id}/replace")
    public DataResponse<UserCardDto> replace(@PathVariable Long id,
                                             @Valid @RequestBody CardNumberPasswordRequestDto cardReplaceRequestDto,
                                             @ApiIgnore UserPrincipal userPrincipal) {
        UserCardDto cardDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/replace")
                .withRequestBody(cardReplaceRequestDto)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<UserCardDto>builder()
                .data(cardDto)
                .build();
    }

    @IsClient
    @GetMapping("/{id}/qr")
    public DataResponse<CardQrDto> getQrCode(@PathVariable Long id,
                                             @ApiIgnore UserPrincipal userPrincipal) {
        CardQrDto cardQrDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cards/qr")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<CardQrDto>builder()
                .success(true)
                .data(cardQrDto)
                .build();
    }
}