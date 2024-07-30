package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.DeliveryEntity;
import ru.sparural.engine.repositories.DeliveryRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Delivery;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {
    private final DSLContext dslContext;

    @Override
    public List<DeliveryEntity> fetch(int offset, int limit, Boolean includeNotPublic) {
        var condition = DSL.noCondition();
        if (!includeNotPublic)
            condition = Delivery.DELIVERY.ISPUBLIC.eq(true)
                    .and(Delivery.DELIVERY.DRAFT.eq(false));
        return dslContext.selectFrom(Delivery.DELIVERY)
                .where(condition)
                .orderBy(Delivery.DELIVERY.TITLE.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(DeliveryEntity.class);
    }

    @Override
    public List<DeliveryEntity> fetch(int offset, int limit) {
        return dslContext.selectFrom(Delivery.DELIVERY)
                .orderBy(Delivery.DELIVERY.TITLE.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(DeliveryEntity.class);
    }

    @Override
    public Optional<DeliveryEntity> get(Long id) throws ResourceNotFoundException {
        return dslContext
                .selectFrom(Delivery.DELIVERY)
                .where(Delivery.DELIVERY.ID.eq(id))
                .fetchOptionalInto(DeliveryEntity.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(Delivery.DELIVERY)
                .where(Delivery.DELIVERY.ID.eq(id))
                .execute() == 1;
    }

    //TODO add photo
    @Override
    public Optional<DeliveryEntity> update(Long id, DeliveryEntity deliveryEntity) throws ResourceNotFoundException {
        return dslContext.update(Delivery.DELIVERY)
                .set(Delivery.DELIVERY.TITLE, coalesce(val(deliveryEntity.getTitle()), Delivery.DELIVERY.TITLE))
                .set(Delivery.DELIVERY.SHORTDESCRIPTION, coalesce(val(deliveryEntity.getShortDescription()), Delivery.DELIVERY.SHORTDESCRIPTION))
                .set(Delivery.DELIVERY.URL, coalesce(val(deliveryEntity.getUrl()), Delivery.DELIVERY.URL))
                .set(Delivery.DELIVERY.ISPUBLIC, coalesce(val(deliveryEntity.isPublic()), Delivery.DELIVERY.ISPUBLIC))
                .set(Delivery.DELIVERY.DRAFT, coalesce(val(deliveryEntity.isDraft()), Delivery.DELIVERY.DRAFT))
                .set(Delivery.DELIVERY.UPDATEDAT, TimeHelper.currentTime())
                .where(Delivery.DELIVERY.ID.eq(id))
                .returning()
                .fetchOptionalInto(DeliveryEntity.class);
    }

    @Override
    public Optional<DeliveryEntity> create(DeliveryEntity deliveryEntity) {
        return dslContext
                .insertInto(Delivery.DELIVERY)
                .set(Delivery.DELIVERY.TITLE, deliveryEntity.getTitle())
                .set(Delivery.DELIVERY.SHORTDESCRIPTION, deliveryEntity.getShortDescription())
                .set(Delivery.DELIVERY.URL, deliveryEntity.getUrl())
                .set(Delivery.DELIVERY.ISPUBLIC, deliveryEntity.isPublic())
                .set(Delivery.DELIVERY.DRAFT, deliveryEntity.isDraft())
                .set(Delivery.DELIVERY.CREATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(DeliveryEntity.class);

    }


}
