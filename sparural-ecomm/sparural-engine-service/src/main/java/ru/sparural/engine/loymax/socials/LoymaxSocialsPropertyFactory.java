package ru.sparural.engine.loymax.socials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import ru.sparural.engine.api.enums.SocialName;
import ru.sparural.engine.loymax.socials.properties.LoymaxAppleSoicalPropertyHolder;
import ru.sparural.engine.loymax.socials.properties.LoymaxFacebookSocialPropertyHolder;
import ru.sparural.engine.loymax.socials.properties.LoymaxOdnoklassnikiSocialPropertyHolder;
import ru.sparural.engine.loymax.socials.properties.LoymaxVkSocialPropertyHolder;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
public class LoymaxSocialsPropertyFactory {

    private Map<SocialName, LoymaxSocial> socials;
    private GenericApplicationContext ctx;

    @Autowired
    public void setCtx(GenericApplicationContext ctx) {
        this.ctx = ctx;
    }

    @PostConstruct
    public void init() {
        socials = Map.of(
                SocialName.VKONTAKTE, ctx.getBean(LoymaxVkSocialPropertyHolder.class),
                SocialName.FACEBOOK, ctx.getBean(LoymaxFacebookSocialPropertyHolder.class),
                SocialName.APPLE, ctx.getBean(LoymaxAppleSoicalPropertyHolder.class),
                SocialName.ODNOKLASSNIKI, ctx.getBean(LoymaxOdnoklassnikiSocialPropertyHolder.class)
        );
    }

    public LoymaxSocial getSocialProperties(String socialName) {
        var socialNameEnum = SocialName.of(socialName)
                .orElseThrow(() -> new ResourceNotFoundException("social not found"));
        return socials.get(socialNameEnum);
    }

    public LoymaxSocial getSocialProperties(SocialName socialNames) {
        return socials.get(socialNames);
    }
}
