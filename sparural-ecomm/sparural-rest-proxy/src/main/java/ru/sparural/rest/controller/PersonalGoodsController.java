package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/personal-goods", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "personal-goods")
public class PersonalGoodsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @GetMapping
    public DataResponse<List<PersonalGoodsDto>> list(@ApiIgnore UserPrincipal userPrincipal,
                                                          @RequestParam(defaultValue = "0") Integer offset,
                                                          @RequestParam(defaultValue = "30") Integer limit) {
        List<PersonalGoodsDto> data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("personal-goods/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("loymaxName", "PersonalOffersGoodsPrice")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        if (data == null) data = new ArrayList<>();
        return DataResponse.<List<PersonalGoodsDto>>builder()
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @GetMapping("/no-price")
    public DataResponse<List<PersonalGoodsDto>> listNoPrice(@ApiIgnore UserPrincipal userPrincipal,
                                                               @RequestParam(defaultValue = "0") Integer offset,
                                                               @RequestParam(defaultValue = "30") Integer limit) {
        List<PersonalGoodsDto> data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("personal-goods/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("loymaxName", "PersonalOffersGoods")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        if (data == null) data = new ArrayList<>();
        return DataResponse.<List<PersonalGoodsDto>>builder()
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @PostMapping("/{id}/accept")
    public DataResponse<PersonalGoodsDto> accept(@PathVariable Long id,
                                                 @ApiIgnore UserPrincipal userPrincipal) {
        PersonalGoodsDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("personal-goods/accept")
                .withRequestParameter("goodsId", id)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();

        return DataResponse.<PersonalGoodsDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }
}
