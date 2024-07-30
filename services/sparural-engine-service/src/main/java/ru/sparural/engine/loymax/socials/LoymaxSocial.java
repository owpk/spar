package ru.sparural.engine.loymax.socials;

import ru.sparural.engine.entity.Social;

/**
 * @author Vorobyev Vyacheslav
 */
public interface LoymaxSocial {
    String getOauthProvider();

    String getOauthProviderLoginPattern();

    String buildLoginPage();

    String buildSetPageUrl();

    String getLoginUrl();

    String getSetUrl();

    String getSocialName();

    String getSetSocialUrl();

    String getRemoveSocialUrl();

    String buildLoginUrl(String code);

    Social getSocialEntity();
}
