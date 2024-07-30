package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.CouponUserEntity;
import ru.sparural.engine.repositories.CouponUserRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.CouponUser;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponUserRepositoryImpl implements CouponUserRepository {
    private final CouponUser table = CouponUser.COUPON_USER;
    private final DSLContext dslContext;

    @Override
    public Optional<CouponUserEntity> checkIfCouponUserExist(Long userId, Long couponId) {
        return dslContext
                .selectFrom(table)
                .where(table.USERID.eq(userId))
                .and(table.COUPONID.eq(couponId))
                .fetchOptionalInto(CouponUserEntity.class);
    }

    @Override
    public List<CouponUserEntity> getCouponUserList(Long userId) {
        return dslContext
                .selectFrom(table)
                .where(table.USERID.eq(userId))
                .orderBy(table.ID.desc())
                .fetch()
                .into(CouponUserEntity.class);
    }

    @Override
    public Optional<CouponUserEntity> saveOrUpdate(CouponUserEntity entity) {
        return dslContext
                .insertInto(table)
                .set(table.COUPONID, entity.getCouponId())
                .set(table.USERID, entity.getUserId())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.USERID, table.COUPONID)
                .doNothing()
                .returning()
                .fetchOptionalInto(CouponUserEntity.class);
    }
}
