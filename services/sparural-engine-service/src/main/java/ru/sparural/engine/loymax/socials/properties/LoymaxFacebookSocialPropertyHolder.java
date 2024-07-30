package ru.sparural.engine.loymax.socials.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.sparural.engine.api.enums.SocialName;
import ru.sparural.engine.loymax.LoymaxConstants;
import ru.sparural.engine.services.SocialsService;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@Scope("prototype")
public class LoymaxFacebookSocialPropertyHolder extends LoymaxSocialsPropertyHolderAbs {

    private static final String OAUTH_URL = "https://www.facebook.com/v12.0/dialog/oauth";
    private static final String OAUTH_PARAMS = "?client_id=%s&redirect_uri=%s";

    @Autowired
    protected LoymaxFacebookSocialPropertyHolder(LoymaxConstants loymaxConstants, SocialsService socialsService) {
        super(loymaxConstants, socialsService);
    }

    @Override
    public String getOauthProvider() {
        return OAUTH_URL;
    }

    @Override
    public String getOauthProviderLoginPattern() {
        return OAUTH_URL + OAUTH_PARAMS;
    }

    @Override
    public String buildSetPageUrl() {
        return String.format(OAUTH_URL + OAUTH_PARAMS, getAppId(), getRedirectSetUrl());
    }

    @Override
    public String getLoginUrl() {
        return loymaxConstants.facebookLoginUrl;
    }

    @Override
    public String getSetUrl() {
        return loymaxConstants.facebookSetUrl;
    }

    @Override
    public String getSocialName() {
        return SocialName.FACEBOOK.getName();
    }
}
