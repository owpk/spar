package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.socials.SocialDto;
import ru.sparural.engine.api.dto.socials.SocialSetDto;
import ru.sparural.engine.api.dto.socials.SocialSettingReqDto;
import ru.sparural.engine.api.dto.socials.SocialUrlDto;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.engine.loymax.LoymaxConstants;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.socials.LoymaxSocialsPropertyFactory;
import ru.sparural.engine.services.AuthorizationService;
import ru.sparural.engine.services.SocialsService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.kafka.exception.KafkaControllerException;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class SocialsController {

    private final SocialsService socialsService;
    private final AuthorizationService authorizationService;
    private final DtoMapperUtils mapperUtils;
    private final LoymaxSocialsPropertyFactory loymaxSocialsContext;
    private final LoymaxService loymaxService;
    private final DtoMapperUtils dtoMapperUtils;
    private final LoymaxConstants loymaxConstants;

    @KafkaSparuralMapping("social/set")
    public Boolean set(@RequestParam Long userId,
                       @RequestParam String social,
                       @Payload SocialSetDto socialSetDto) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.userSetSocial(social, loymaxUser, socialSetDto);
        return true;
    }

    @KafkaSparuralMapping("social/remove")
    public Boolean remove(@RequestParam Long userId,
                          @RequestParam String social) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.userRemoveSocial(loymaxUser, social);
        return true;
    }

    @KafkaSparuralMapping("socials/get")
    public List<SocialDto> get() {
        return mapperUtils.convertList(SocialDto.class, socialsService.get());
    }

    @KafkaSparuralMapping("socials/update")
    public SocialDto update(@Payload SocialSettingReqDto socialDto, @RequestParam Long id) {
        return socialsService.update(id, socialDto);
    }

    @KafkaSparuralMapping("socials/get-url")
    public SocialUrlDto getUrlByName(@RequestParam String social, @RequestParam String target) {
        var socialProperties = loymaxSocialsContext.getSocialProperties(social);
        String result = null;
        if (target.equals("set")) {
            result = socialProperties.buildSetPageUrl();
        } else if (target.equals("login")) {
            if (social.equals("apple")) {
                return new SocialUrlDto(String.format("https://appleid.apple.com/auth/authorize?client_id=ru.sparural.mobile.loymax&response_type=code&redirect_uri=%s/socials/apple/login-success.html", loymaxConstants.getSparRedirectUrlPattern()));
            }
            result = socialProperties.buildLoginPage();
        }
        return new SocialUrlDto(result);
    }

    @KafkaSparuralMapping("socials/login")
    public TokenDataDto login(@RequestParam String social, @RequestParam String code) throws KafkaControllerException {
        return authorizationService.authorizeViaSocials(code, social);
    }

    @KafkaSparuralMapping("social/get-binding")
    public List<SocialDto> getUserSocialBindings(@RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        return dtoMapperUtils.convertList(
                SocialDto.class, loymaxService.getSocialsBindings(loymaxUser));
    }
}
