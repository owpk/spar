package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.CityDto;
import ru.sparural.engine.api.dto.CoordinatesDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/cities", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@Api(tags = "cities")
public class CitiesController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<CityDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                            @RequestParam(defaultValue = "30") Integer limit) {
        List<CityDto> cityDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cities/list")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return new DataResponse<>(cityDto);
    }

    @PostMapping("/by-location")
    public DataResponse<CityDto> update(@Valid @RequestBody CoordinatesDto restRequest,
                                        @ApiIgnore UserPrincipal userPrincipal) {
        Long userId = 0L;
        if (userPrincipal != null) {
            userId = userPrincipal.getUserId();
        }
        CityDto cityDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("cities/by-location")
                .withRequestBody(restRequest)
                .withRequestParameter("userId", userId)
                .sendForEntity();
        return new DataResponse<>(cityDto);
    }
}
