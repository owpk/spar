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
                .where(table.PERSONAL_OFFER_ID.eq(offerId))
                .and(table.USER_ID.eq(userId))
                .fetchOptionalInto(PersonalOfferUserEntity.class);
    }

    @Override
    public Optional<PersonalOfferUserEntity> create(PersonalOfferUserEntity personalOfferUserEntity) {
        return dslContext
                .insertInto(table)
                .set(table.USER_ID, personalOfferUserEntity.getUserId())
                .set(table.PERSONAL_OFFER_ID, personalOfferUserEntity.getPersonalOfferId())
                .set(table.DATA, personalOfferUserEntity.getData())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.USER_ID, table.PERSONAL_OFFER_ID)
                .doUpdate()
                .set(table.USER_ID, personalOfferUserEntity.getUserId())
                .set(table.PERSONAL_OFFER_ID, personalOfferUserEntity.getPersonalOfferId())
                .set(table.DATA, personalOfferUserEntity.getData())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(PersonalOfferUserEntity.class);
    }

    @Override
    public Optional<PersonalOfferUserEntity> updateData(PersonalOfferUserEntity personalOfferUserEntity) {
        return dslContext
                .update(table)
                .set(table.DATA, personalOfferUserEntity.getData())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .where(table.USER_ID.eq(personalOfferUserEntity.getUserId()))
                .and(table.PERSONAL_OFFER_ID.eq(personalOfferUserEntity.getPersonalOfferId()))
                .returning()
                .fetchOptionalInto(PersonalOfferUserEntity.class);
    }

    @Override
    public List<PersonalOfferUserEntity> listByUserId(Long userId) {
        return dslContext.selectFrom(table)
                .where(table.USER_ID.eq(userId))
                .orderBy(table.ID.desc())
                .fetch().into(PersonalOfferUserEntity.class);
    }
}
