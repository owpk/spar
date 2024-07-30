package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.socials.SocialDto;
import ru.sparural.engine.api.dto.socials.SocialSettingReqDto;
import ru.sparural.engine.api.dto.socials.SocialUrlDto;
import ru.sparural.engine.api.dto.socials.SocialsLoginRequest;
import ru.sparural.gobals.Constants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.Authenticated;
import ru.sparural.rest.security.annotations.IsAdmin;
import ru.sparural.rest.services.AuthorizationService;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.dto.JwtResponse;
import ru.sparural.rest.security.TokenManager;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/socials", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "socials")
public class SocialsController {

    private final SparuralKafkaRequestCreator restToKafka;
    private final KafkaTopics kafkaTopics;
    private final AuthorizationService authorizationService;
    private final TokenManager tokenManager;

    @GetMapping
    @IsAdmin
    @ResponseType(ControllerResponseType.WRAPPED)
    public List<SocialDto> get() {
        return restToKafka.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("socials/get")
                .sendForEntity();
    }

    @PutMapping("/{id}")
    @IsAdmin
    @ResponseType(ControllerResponseType.WRAPPED)
    public SocialDto update(@Valid @Parameter @RequestBody DataRequest<SocialSettingReqDto> restRequest,
                                          @PathVariable Long id) {
        return restToKafka.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("socials/update")
                .withRequestBody(restRequest.getData())
                .withRequestParameter("id", id)
                .sendForEntity();
    }

    @GetMapping("/{social}/url")
    @Authenticated
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public SocialUrlDto socialsAuthorize(@PathVariable @Valid @NotNull(message = "Social name required") String social,
                                                              @RequestParam(defaultValue = "login")
                                                              @Pattern(regexp = "(set|login)",
                                                                      message = "Required target values set or login") String target) {
        return restToKafka.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("socials/get-url")
                .withRequestParameter("social", social)
                .withRequestParameter("target", target)
                .sendForEntity();
    }

    @PostMapping("/{social}/login")
    @Authenticated
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public JwtResponse socialsLogin(@Parameter(description = "web/mobile")
                                          @RequestHeader(value = Constants.CLIENT_TYPE_HEADER_NAME,
                                                  defaultValue = Constants.CLIENT_TYPE_MOBILE) String client,
                                          @RequestBody DataRequest<SocialsLoginRequest> restRequest,
                                          @Valid @NotNull(message = "Social name required") @PathVariable String social,
                                          @ApiIgnore HttpServletResponse response,
                                          @ApiIgnore UserPrincipal principal) {
        var token = authorizationService.loginViaSocials(social, restRequest.getData().getCode());
        if (Constants.CLIENT_TYPE_WEB.equals(client)) {
            response.addCookie(tokenManager.createSecuredCookie(Constants.ACCESS_TOKEN, token.getAccessToken(), tokenManager.getJwtExpiresIn()));
            response.addCookie(tokenManager.createSecuredCookie(Constants.REFRESH_TOKEN, token.getRefreshToken(), tokenManager.getRefreshExpiresIn()));
        }

        return token;
    }
}