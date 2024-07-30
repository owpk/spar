package ru.sparural.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.MerchantDto;
import ru.sparural.engine.api.dto.MerchantUpdateDto;
import ru.sparural.engine.api.dto.merchant.Merchants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/merchants", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "merchants")
public class MerchantController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;
    private final ObjectMapper objectMapper;

    @GetMapping
    public DataResponse<List<Merchants>> list(@RequestParam(defaultValue = "30") Integer limit,
                                              @RequestParam(defaultValue = "0") Integer offset,
                                              @RequestParam(required = false) Double topLeftLongitude,
                                              @RequestParam(required = false) Double topLeftLatitude,
                                              @RequestParam(required = false) Double bottomRightLongitude,
                                              @RequestParam(required = false) Double bottomRightLatitude,
                                              @RequestParam(required = false) Double userLongitude,
                                              @RequestParam(required = false) Double userLatitude,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(required = false) Long[] format,
                                              @RequestParam(required = false) Long[] attributes,
                                              @ApiIgnore UserPrincipal userPrincipal) throws JsonProcessingException {


        Long userId = 0L;
        List<String> roles = new ArrayList<>();
        if (userPrincipal != null) {
            userId = userPrincipal.getUserId();
            roles = userPrincipal.getSecuredRoles();
        }
        List<Merchants> resp = restToKafkaService.createRequestBuilder()
                .withAction("merchants/index")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("limit", limit)
                .withRequestParameter("offset", offset)
                .withRequestParameter("topLeftLongitude", topLeftLongitude)
                .withRequestParameter("topLeftLatitude", topLeftLatitude)
                .withRequestParameter("bottomRightLongitude", bottomRightLongitude)
                .withRequestParameter("bottomRightLatitude", bottomRightLatitude)
                .withRequestParameter("userLongitude", userLongitude)
                .withRequestParameter("userLongitude", userLongitude)
                .withRequestParameter("userLatitude", userLatitude)
                .withRequestParameter("status", status)
                .withRequestParameter("format", objectMapper.writeValueAsString(format))
                .withRequestParameter("attributes", objectMapper.writeValueAsString(attributes))
                .withRequestParameter("userId", userId)
                .withRequestParameter("userRoles", roles)
                .sendForEntity();
        if (resp.isEmpty()) return DataResponse.<List<Merchants>>builder()
                .success(true)
                .data(new ArrayList<>())
                .meta("Merchants not found in this area")
                .version(Constants.VERSION)
                .build();
        return DataResponse.<List<Merchants>>builder()
                .success(true)
                .data(resp)
                .version(Constants.VERSION)
                .build();
    }

    @GetMapping("/{id}")
    public DataResponse<Merchants> get(@PathVariable Long id, @ApiIgnore UserPrincipal userPrincipal) {
        Long userId = 0L;
        if (userPrincipal != null) {
            userId = userPrincipal.getUserId();
        }
        Merchants resp = restToKafkaService.createRequestBuilder()
                .withAction("merchants/get")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("id", id)
                .withRequestParameter("userId", userId)
                .sendForEntity();
        return DataResponse.<Merchants>builder()
                .success(true)
                .data(resp)
                .version(Constants.VERSION)
                .build();
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<Merchants> create(@Valid @RequestBody DataRequest<MerchantDto> createMerchantRequestDto,
                                          @ApiIgnore UserPrincipal userPrincipal) {
        Merchants resp = restToKafkaService.createRequestBuilder()
                .withAction("merchants/create")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestBody(createMerchantRequestDto.getData())
                .sendForEntity();
        return DataResponse.<Merchants>builder()
                .success(true)
                .data(resp)
                .version(Constants.VERSION)
                .build();
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<Merchants> update(@PathVariable Long id,
                                          @Valid @RequestBody DataRequest<MerchantUpdateDto> updateDto) {
        if (updateDto.getData().getWorkingStatus() != null
                && updateDto.getData().getWorkingStatus().equals("ClosedUntil")) {
            updateDto.getData().setWorkingStatus("Open");
        }
        Merchants resp = restToKafkaService.createRequestBuilder()
                .withAction("merchants/update")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("id", id)
                .withRequestBody(updateDto.getData())
                .sendForEntity();

        return DataResponse.<Merchants>builder()
                .success(true)
                .data(resp)
                .version(Constants.VERSION)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<Void> delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchants/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();

    }

}
