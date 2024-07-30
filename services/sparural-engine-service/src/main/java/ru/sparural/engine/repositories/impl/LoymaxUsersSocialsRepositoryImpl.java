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
                .set(table.USERID, entity.getUserId())
                .set(table.LOYMAXSOCIALUSERID, entity.getLoymaxSocialUserId())
                .set(table.SOCIALID, entity.getSocialId())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.LOYMAXSOCIALUSERID, table.USERID, table.SOCIALID)
                .doNothing().execute();
        log.debug("Inserting loymax users socials entity, result: " + result);
    }
}