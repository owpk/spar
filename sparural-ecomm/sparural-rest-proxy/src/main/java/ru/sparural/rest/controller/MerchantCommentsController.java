package ru.sparural.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.MerchantCommentDto;
import ru.sparural.engine.api.dto.MerchantCommentsDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.CSVWriter;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/merchant-comments", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "merchants")
public class MerchantCommentsController {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final ObjectMapper objectMapper;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @PostMapping
    UnwrappedGenericDto<Void> create(@Valid @RequestBody MerchantCommentsDto merchantCommentDto,
                                     @ApiIgnore UserPrincipal userPrincipal) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments/create")
                .withRequestBody(merchantCommentDto)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();

    }

    /**
     * search - поисковая строка. Поиск вхождения подстроки в ФИО пользователя и по полю comment
     * grade[] - оценка. Фильтр по полю grade. Можно указать несколько значений
     * dateTimeStart - начало периода, формат timestamp
     * dateTimeEnd - окончание периода, формат timestamp
     * merchantId[] - идентификатор магазина. Можно указать несколько значений
     * Параметры работают точно так же, как в методе получения коллекции отзывов.
     */
    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<MerchantCommentDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                       @RequestParam(defaultValue = "30") Integer limit,
                                                       @RequestParam(required = false) String search,
                                                       @RequestParam(required = false) Integer[] grade,
                                                       @RequestParam(required = false) Long dateTimeStart,
                                                       @RequestParam(required = false) Long dateTimeEnd,
                                                       @RequestParam(required = false) Long[] merchantId) throws JsonProcessingException {
        List<MerchantCommentDto> merchantComments = restToKafkaService.createRequestBuilder()
                .withAction("merchant-comments/index")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("offset", offset)
                .withRequestParameter("search", search)
                .withRequestParameter("grade", objectMapper.writeValueAsString(grade))
                .withRequestParameter("dateTimeStart", dateTimeStart)
                .withRequestParameter("dateTimeEnd", dateTimeEnd)
                .withRequestParameter("merchantId", objectMapper.writeValueAsString(merchantId))
                .withRequestParameter("limit", limit)
                .sendForEntity();

        return DataResponse.<List<MerchantCommentDto>>builder()
                .success(true)
                .data(merchantComments)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping("/{id}")
    public DataResponse<MerchantCommentDto> get(@PathVariable Long id) {
        MerchantCommentDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments/get")
                .withRequestParameter("id", id)
                .sendForEntity();

        return DataResponse.<MerchantCommentDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping(value = "/export", produces = "text/csv")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) Integer[] grade,
                       @RequestParam(required = false) Long dateTimeStart,
                       @RequestParam(required = false) Long dateTimeEnd,
                       @RequestParam(required = false) Long[] merchantId) throws IOException {
        List<MerchantCommentDto> merchantComments = restToKafkaService.createRequestBuilder()
                .withAction("merchant-comments/index")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("offset", 0)
                .withRequestParameter("search", search)
                .withRequestParameter("grade", objectMapper.writeValueAsString(grade))
                .withRequestParameter("dateTimeStart", dateTimeStart)
                .withRequestParameter("dateTimeEnd", dateTimeEnd)
                .withRequestParameter("merchantId", objectMapper.writeValueAsString(merchantId))
                .withRequestParameter("limit", -1)
                .sendForEntity();
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setHeader("Content-Disposition",
                String.format("attachment; filename=\"%s\"",
                        "merchant-comments.csv"));
        CSVWriter.writeCSVContent(response.getWriter(), merchantComments);
    }
}