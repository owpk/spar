package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.PersonalOfferUserEntity;
import ru.sparural.engine.repositories.PersonalOfferUserRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.PersonalOfferUser;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonalOfferUserRepositoryImpl implements PersonalOfferUserRepository {

    private final DSLContext dslContext;
    private final PersonalOfferUser table = PersonalOfferUser.PERSONAL_OFFER_USER;

    @Override
    public Optional<PersonalOfferUserEntity> getByUserIdAndOfferId(Long userId, Long offerId) {
        return dslContext
                .selectFrom(table)
                .where(table.PERSONALOFFERID.eq(offerId))
                .and(table.USERID.eq(userId))
                .fetchOptionalInto(PersonalOfferUserEntity.class);
    }

    @Override
    public Optional<PersonalOfferUserEntity> create(PersonalOfferUserEntity personalOfferUserEntity) {
        return dslContext
                .insertInto(table)
                .set(table.USERID, personalOfferUserEntity.getUserId())
                .set(table.PERSONALOFFERID, personalOfferUserEntity.getPersonalOfferId())
                .set(table.DATA, personalOfferUserEntity.getData())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.USERID, table.PERSONALOFFERID)
                .doUpdate()
                .set(table.USERID, personalOfferUserEntity.getUserId())
                .set(table.PERSONALOFFERID, personalOfferUserEntity.getPersonalOfferId())
                .set(table.DATA, personalOfferUserEntity.getData())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(PersonalOfferUserEntity.class);
    }

    @Override
    public Optional<PersonalOfferUserEntity> updateData(PersonalOfferUserEntity personalOfferUserEntity) {
        return dslContext
                .update(table)
                .set(table.DATA, personalOfferUserEntity.getData())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .where(table.USERID.eq(personalOfferUserEntity.getUserId()))
                .and(table.PERSONALOFFERID.eq(personalOfferUserEntity.getPersonalOfferId()))
                .returning()
                .fetchOptionalInto(PersonalOfferUserEntity.class);
    }

    @Override
    public List<PersonalOfferUserEntity> listByUserId(Long userId) {
        return dslContext.selectFrom(table)
                .where(table.USERID.eq(userId))
                .orderBy(table.ID.desc())
                .fetch().into(PersonalOfferUserEntity.class);
    }
}
