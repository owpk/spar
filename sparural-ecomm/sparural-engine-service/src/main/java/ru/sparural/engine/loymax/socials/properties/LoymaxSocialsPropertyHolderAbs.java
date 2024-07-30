package ru.sparural.engine.loymax.socials.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sparural.engine.entity.Social;
import ru.sparural.engine.loymax.LoymaxConstants;
import ru.sparural.engine.loymax.socials.LoymaxSocial;
import ru.sparural.engine.services.SocialsService;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
public abstract class LoymaxSocialsPropertyHolderAbs implements LoymaxSocial {
    public static final String PATTERN_TOKEN = "$";

    protected final LoymaxConstants loymaxConstants;
    protected final SocialsService socialsService;
    protected Social social;

    @Autowired
    protected LoymaxSocialsPropertyHolderAbs(LoymaxConstants loymaxConstants,
                                             SocialsService socialsService) {
        this.loymaxConstants = loymaxConstants;
        this.socialsService = socialsService;
        load();
    }

    protected void load() {
        this.social = socialsService.getBySocialName(getSocialName());
    }

    public String getAppId() {
        return social.getAppId();
    }

    public String getSetSocialUrl() {
        return String.format(loymaxConstants.userSetSocialPattern, getSocialName());
    }

    public String getRemoveSocialUrl() {
        return String.format(loymaxConstants.userRemoveSocialPattern, getSocialName());
    }

    public String buildLoginUrl(String code) {
        return String.format("%s?code=%s&redirect_uri=%s", getLoginUrl(), code, getRedirectLoginUrl());
    }

    public String buildLoginPage() {
        return String.format(getOauthProviderLoginPattern(), getAppId(), getRedirectLoginUrl());
    }

    public String getRedirectLoginUrl() {
        return replaceFromUrl(loymaxConstants.getSparRedirectUrlPattern(), getSocialName());
    }

    public String getRedirectSetUrl() {
        return replaceFromUrl(loymaxConstants.getSparRedirectSetUrlPattern(), getSocialName());
    }

    protected String replaceFromUrl(String url, String replacement) {
        return url.replace(PATTERN_TOKEN, replacement);
    }

    @Override
    public Social getSocialEntity() {
        return social;
    }
}