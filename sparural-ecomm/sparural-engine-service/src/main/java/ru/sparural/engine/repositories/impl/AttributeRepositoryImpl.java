package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.AttributeEntity;
import ru.sparural.engine.entity.MerchantAttribute;
import ru.sparural.engine.repositories.AttributeRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.MerchantAttributeMerchant;
import ru.sparural.tables.MerchantAttributes;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttributeRepositoryImpl implements AttributeRepository {
    private final DSLContext dslContext;
    private final MerchantAttributes table = MerchantAttributes.MERCHANT_ATTRIBUTES;

    @Override
    public Optional<AttributeEntity> get(Long id) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(AttributeEntity.class);
    }

    @Override
    public List<AttributeEntity> list(Integer offset, Integer limit) {
        return dslContext.selectFrom(table)
                .orderBy(table.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(AttributeEntity.class);
    }

    @Override
    public Optional<AttributeEntity> create(AttributeEntity entity) {
        return dslContext.insertInto(table)
                .set(table.NAME, entity.getName())
                .set(table.DRAFT, entity.getDraft())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(AttributeEntity.class);
    }

    @Override
    public Optional<AttributeEntity> update(Long id, AttributeEntity entity) {
        return dslContext.update(table)
                .set(table.NAME, entity.getName())
                .set(table.DRAFT, entity.getDraft())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .where(table.ID.eq(id))
                .returning()
                .fetchOptionalInto(AttributeEntity.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table)
                .where(table.ID.eq(id))
                .execute() == 1;
    }

    @Override
    public void deleteAllForMerchant(Long merchantId) {
        dslContext.delete(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT)
                .where(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.MERCHANTS_ID.eq(merchantId))
                .execute();
    }

    @Override
    public Boolean findDraftById(Long id) {
        return dslContext.select(table.DRAFT)
                .from(table)
                .where(table.ID.eq(id))
                .fetchSingle(table.DRAFT);
    }

    @Override
    public List<MerchantAttribute> listOfMerchants(Long id) {
        return dslContext.select(MerchantAttributes.MERCHANT_ATTRIBUTES.ID)
                .select(MerchantAttributes.MERCHANT_ATTRIBUTES.NAME)
                .select(MerchantAttributes.MERCHANT_ATTRIBUTES.DRAFT)
                .from(MerchantAttributes.MERCHANT_ATTRIBUTES
                        .leftJoin(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT)
                        .on(MerchantAttributes.MERCHANT_ATTRIBUTES.ID
                                .eq(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.ATTRIBUTE_ID)))
                .where(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.MERCHANTS_ID
                        .eq(id))
                .fetchInto(MerchantAttribute.class);
    }

    @Override
    public List<Long> listIdOfMerchants(Long id) {
        return dslContext.select(MerchantAttributes.MERCHANT_ATTRIBUTES.ID)
                .from(MerchantAttributes.MERCHANT_ATTRIBUTES
                        .leftJoin(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT)
                        .on(MerchantAttributes.MERCHANT_ATTRIBUTES.ID
                                .eq(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.ATTRIBUTE_ID)))
                .where(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.MERCHANTS_ID
                        .eq(id))
                .fetchInto(Long.class);
    }

    @Override
    public void saveMerchantAttributesOfMerchant(Long attributeId, Long merchantId) {
        dslContext.insertInto(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT)
                .set(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.ATTRIBUTE_ID, attributeId)
                .set(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.MERCHANTS_ID, merchantId)
                .set(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.CREATED_AT, TimeHelper.currentTime())
                .set(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.UPDATED_AT, TimeHelper.currentTime())
                .execute();
    }


}
