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
public class LoymaxAppleSoicalPropertyHolder extends LoymaxSocialsPropertyHolderAbs {

    private static final String OAUTH_URL = "https://appleid.apple.com/auth/authorize";
    private static final String OAUTH_PARAMS = "?client_id=%s&response_type=code&redirect_uri=%s";

    @Autowired
    protected LoymaxAppleSoicalPropertyHolder(LoymaxConstants loymaxConstants, SocialsService socialsService) {
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
        return String.format(OAUTH_URL + OAUTH_PARAMS + "/socials/apple/binding-success.html", getAppId(), loymaxConstants.getSparRedirectUrlPattern());
    }

    @Override
    public String getLoginUrl() {
        return loymaxConstants.appleLoginUrl;
    }

    @Override
    public String getSetUrl() {
        return loymaxConstants.appleSetUrl;
    }

    @Override
    public String getSocialName() {
        return SocialName.APPLE.getName();
    }
}
