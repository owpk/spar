package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sparural.engine.api.dto.CatalogDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/catalogs", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "catalog")
public class CatalogsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    public DataResponse<List<CatalogDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                               @RequestParam(defaultValue = "30") Integer limit,
                                               @RequestParam(defaultValue = "0", required = false) Integer city) {
        List<CatalogDto> catalogDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("catalogs/index")
                .withRequestParameters(Map.of("offset", offset, "limit", limit, "city", city))
                .sendForEntity();
        return new DataResponse<>(catalogDto);
    }

    @GetMapping("/{id}")
    public DataResponse<CatalogDto> get(@PathVariable Long id) {
        CatalogDto catalogDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("catalogs/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return new DataResponse<>(catalogDto);
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<Boolean> delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("catalogs/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Boolean>builder()
                .success(success)
                .build();
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<CatalogDto> update(@PathVariable Long id,
                                           @Valid @RequestBody DataRequest<CatalogDto> request) {
        CatalogDto catalogDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("catalogs/update")
                .withRequestParameter("id", id)
                .withRequestBody(request.getData())
                .sendForEntity();
        return new DataResponse<>(catalogDto);
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<CatalogDto> create(@Valid @RequestBody DataRequest<CatalogDto> request) {
        CatalogDto catalogDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("catalogs/create")
                .withRequestBody(request.getData())
                .sendForEntity();
        return new DataResponse<>(catalogDto);
    }

    // TODO FILE STORAGE SERVICE IMPLEMENTATION NEEDED !!!
    @PostMapping("/{id}/photo")
    public DataResponse<EmptyObject> photo(@PathVariable Long id,
                                           @RequestParam("file") MultipartFile file,
                                           ModelMap modelMap) {
        throw new UnsupportedOperationException("method not implemented yet");
    }

    @GetMapping("/select-by-coordinates")
    public DataResponse<CatalogDto> select(
            @RequestParam(required = false) String userLongitude,
            @RequestParam(required = false) String userLatitude,
            @ApiIgnore  UserPrincipal userPrincipal) {
        CatalogDto catalogDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("catalogs/update")
                .withRequestParameter("userId", userPrincipal != null ? userPrincipal.getUserId() : 0)
                .withRequestParameter("userLongitude", userLongitude)
                .withRequestParameter("userLatitude", userLatitude)
                .sendForEntity();
        return new DataResponse<>(catalogDto);
    }

}