package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.LoymaxUsersSocialsEntity;
import ru.sparural.engine.repositories.LoymaxUsersSocialsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.LoymaxUsersSocials;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoymaxUsersSocialsRepositoryImpl implements LoymaxUsersSocialsRepository {
    private final DSLContext dsl;
    private final LoymaxUsersSocials table = LoymaxUsersSocials.LOYMAX_USERS_SOCIALS;

    @Override
    @Transactional
    public void saveOrUpdate(LoymaxUsersSocialsEntity entity) {
        var result = dsl.insertInto(table)
                .set(table.USER_ID, entity.getUserId())
                .set(table.LOYMAX_SOCIAL_USER_ID, entity.getLoymaxSocialUserId())
                .set(table.SOCIAL_ID, entity.getSocialId())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.LOYMAX_SOCIAL_USER_ID, table.USER_ID, table.SOCIAL_ID)
                .doNothing().execute();
        log.debug("Inserting loymax users socials entity, result: " + result);
    }
}