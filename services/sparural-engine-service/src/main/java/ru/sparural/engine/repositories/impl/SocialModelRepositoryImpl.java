package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Social;
import ru.sparural.engine.repositories.SocialModelRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Socials;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialModelRepositoryImpl implements SocialModelRepository {

    private final DSLContext dslContext;

    public List<Social> getSocialList() {
        var soc = dslContext.selectFrom(Socials.SOCIALS).fetch();
        return soc.into(Social.class);
    }

    public Social updateById(Long id, Social data) {
        var update = dslContext.update(Socials.SOCIALS)
                .set(Socials.SOCIALS.APPID, data.getAppId())
                .set(Socials.SOCIALS.APPSECRET, data.getAppSecret())
                .set(Socials.SOCIALS.UPDATEDAT, TimeHelper.currentTime())
                .where(Socials.SOCIALS.ID.eq(id))
                .returning().fetchOne();
        if (update == null)
            return null;
        return update.into(Social.class);
    }

    @Override
    public Optional<Social> getSocialName(String name) {
        return dslContext.selectFrom(Socials.SOCIALS)
                .where(Socials.SOCIALS.NAME.eq(name))
                .fetchOptionalInto(Social.class);
    }
}
