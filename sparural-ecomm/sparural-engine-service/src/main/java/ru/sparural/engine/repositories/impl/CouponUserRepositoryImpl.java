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
                .where(table.USER_ID.eq(userId))
                .and(table.COUPON_ID.eq(couponId))
                .fetchOptionalInto(CouponUserEntity.class);
    }

    @Override
    public List<CouponUserEntity> getCouponUserList(Long userId) {
        return dslContext
                .selectFrom(table)
                .where(table.USER_ID.eq(userId))
                .orderBy(table.ID.desc())
                .fetch()
                .into(CouponUserEntity.class);
    }

    @Override
    public Optional<CouponUserEntity> saveOrUpdate(CouponUserEntity entity) {
        return dslContext
                .insertInto(table)
                .set(table.COUPON_ID, entity.getCouponId())
                .set(table.USER_ID, entity.getUserId())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.USER_ID, table.COUPON_ID)
                .doNothing()
                .returning()
                .fetchOptionalInto(CouponUserEntity.class);
    }
}
