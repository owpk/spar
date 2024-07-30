package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.PersonalOffer;
import ru.sparural.engine.repositories.PersonalOffersRepository;
import ru.sparural.tables.PersonalOffers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class PersonalOffersRepositoryImpl implements PersonalOffersRepository {
    private final DSLContext dslContext;
    private final PersonalOffers table = PersonalOffers.PERSONAL_OFFERS;

    @Override
    public List<PersonalOffer> fetch(int offset, int limit) {
        return dslContext.selectFrom(table)
                .where(table.DRAFT.eq(false))
                .orderBy(table.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(PersonalOffer.class);
    }

    @Override
    public Optional<PersonalOffer> get(Long id) {
        return dslContext
                .selectFrom(table)
                .where(table.ID
                        .eq(id))
                .fetchOptionalInto(PersonalOffer.class);
    }

    @Override
    public Optional<PersonalOffer> getByAttribute(String attribute) {
        return dslContext
                .selectFrom(table)
                .where(table.ATTRIBUTE
                        .eq(attribute))
                .fetchOptionalInto(PersonalOffer.class);
    }

    @Override
    public Optional<PersonalOffer> create(PersonalOffer entity) {

        return dslContext
                .insertInto(table)
                .set(table.ATTRIBUTE, entity.getAttribute())
                .set(table.TITLE, entity.getTitle())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.ISPUBLIC, entity.getIsPublic() != null && entity.getIsPublic())
                .set(table.DRAFT, entity.getDraft() == null || entity.getDraft())
                .set(table.BEGIN, entity.getBegin())
                .set(table.END, entity.getEnd())
                .set(table.CREATEDAT, new Date().getTime())
                .returning()
                .fetchOptionalInto(PersonalOffer.class);
    }

    @Override
    public Optional<PersonalOffer> update(Long id, PersonalOffer entity) {

        return dslContext
                .update(table)
                .set(table.ATTRIBUTE, coalesce(val(entity.getAttribute()), table.ATTRIBUTE))
                .set(table.TITLE, coalesce(val(entity.getTitle()), table.TITLE))
                .set(table.DESCRIPTION, coalesce(val(entity.getDescription()), table.DESCRIPTION))
                .set(table.BEGIN, coalesce(val(entity.getBegin()), table.BEGIN))
                .set(table.END, coalesce(val(entity.getEnd()), table.END))
                .set(table.ISPUBLIC, coalesce(val(entity.getIsPublic()), table.ISPUBLIC))
                .set(table.DRAFT, coalesce(val(entity.getDraft()), table.DRAFT))
                .set(table.UPDATEDAT, new Date().getTime())
                .where(table.ID
                        .eq(id))
                .returning()
                .fetchOptionalInto(PersonalOffer.class);
    }


    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table)
                .where(table.ID.eq(id))
                .execute() == 1;
    }

    public Boolean getDraftById(Long id) {
        return dslContext.select(table.DRAFT)
                .from(table)
                .where(table.ID.eq(id))
                .fetchSingle(table.DRAFT);
    }

}
