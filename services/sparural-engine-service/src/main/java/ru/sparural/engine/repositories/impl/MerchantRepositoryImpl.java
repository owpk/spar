package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxMerchant;
import ru.sparural.engine.entity.Merchant;
import ru.sparural.engine.entity.MerchantAttribute;
import ru.sparural.engine.repositories.MerchantRepository;
import ru.sparural.engine.repositories.impl.tools.ConditionBuilder;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.enums.MerchantWorkingStatuses;
import ru.sparural.tables.MerchantAttributeMerchant;
import ru.sparural.tables.MerchantAttributes;
import ru.sparural.tables.Merchants;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.val;
import static ru.sparural.engine.repositories.impl.tools.SearchOperators.MAX;
import static ru.sparural.engine.repositories.impl.tools.SearchOperators.MIN;

@Service
@RequiredArgsConstructor
public class MerchantRepositoryImpl implements MerchantRepository {

    private final DSLContext dslContext;
    private final Merchants table = Merchants.MERCHANTS;

    @Override
    public Optional<Merchant> saveOrUpdate(Merchant createMerchantRequest) {
        return dslContext.insertInto(table)
                .set(table.ADDRESS, createMerchantRequest.getAddress())
                .set(table.LONGITUDE, createMerchantRequest.getLongitude())
                .set(table.LATITUDE, createMerchantRequest.getLatitude())
                .set(table.FORMATID, createMerchantRequest.getFormatId())
                .set(table.TITLE, createMerchantRequest.getTitle())
                .set(table.WORKINGHOURSFROM, createMerchantRequest.getWorkingHoursFrom())
                .set(table.WORKINGHOURSTO, createMerchantRequest.getWorkingHoursTo())
                .set(table.WORKINGSTATUS, createMerchantRequest.getWorkingStatus())
                .set(table.LOYMAXLOCATIONID, createMerchantRequest.getLoymaxLocationId())
                .set(table.ISPUBLIC, createMerchantRequest.getIsPublic())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.LOYMAXLOCATIONID)
                .doUpdate()
                .set(table.ADDRESS, createMerchantRequest.getAddress())
                .set(table.LONGITUDE, createMerchantRequest.getLongitude())
                .set(table.LATITUDE, createMerchantRequest.getLatitude())
                .set(table.FORMATID, createMerchantRequest.getFormatId())
                .set(table.TITLE, createMerchantRequest.getTitle())
                .set(table.WORKINGHOURSFROM, createMerchantRequest.getWorkingHoursFrom())
                .set(table.WORKINGHOURSTO, createMerchantRequest.getWorkingHoursTo())
                .set(table.WORKINGSTATUS, createMerchantRequest.getWorkingStatus())
                .set(table.ISPUBLIC, createMerchantRequest.getIsPublic())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Merchant.class);
    }

    @Override
    public Optional<Merchant> get(Long id) {

        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(Merchant.class);

    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table)
                .where(table.ID.eq(id))
                .execute() == 1;
    }


    @Override
    public Optional<Merchant> update(Long id, Merchant entity) {

        var step = dslContext.update(table)
                .set(table.TITLE, coalesce(val(entity.getTitle()), table.TITLE))
                .set(table.ADDRESS, coalesce(val(entity.getAddress()), table.ADDRESS))
                .set(table.LONGITUDE, coalesce(val(entity.getLongitude()), table.LONGITUDE))
                .set(table.LATITUDE, coalesce(val(entity.getLatitude()), table.LATITUDE))
                .set(table.FORMATID, coalesce(val(entity.getFormatId()), table.FORMATID))
                .set(table.WORKINGHOURSFROM, coalesce(val(entity.getWorkingHoursFrom()), table.WORKINGHOURSFROM))
                .set(table.WORKINGHOURSTO, coalesce(val(entity.getWorkingHoursTo()), table.WORKINGHOURSTO))
                .set(table.LOYMAXLOCATIONID, coalesce(val(entity.getLoymaxLocationId()), table.LOYMAXLOCATIONID))
                .set(table.ISPUBLIC, coalesce(val(entity.getIsPublic()), table.ISPUBLIC))
                .set(table.UPDATEDAT, TimeHelper.currentTime());

        if (entity.getWorkingStatus() != null) {
            step.set(table.WORKINGSTATUS, entity.getWorkingStatus());
        }

        return step
                .where(table.ID.eq(id))
                .returning()
                .fetchOptionalInto(Merchant.class);
    }

    @Override
    public List<Merchant> list(Integer offset,
                               Integer limit,
                               Double topLeftLongitude,
                               Double topLeftLatitude,
                               Double bottomRightLongitude,
                               Double bottomRightLatitude,
                               String status,
                               Long[] format,
                               Long[] attributes,
                               Boolean isAdmin) {

        var step = dslContext.select()
                .from(table)
                .where();

        var conditionBuilder = new ConditionBuilder(step);

        var res = conditionBuilder
                .addCondition(table.LONGITUDE.getName(), topLeftLongitude, MIN)
                .addCondition(table.LONGITUDE.getName(), bottomRightLongitude, MAX)
                .addCondition(table.LATITUDE.getName(), topLeftLatitude, MAX)
                .addCondition(table.LATITUDE.getName(), bottomRightLatitude, MIN)
                .buildCondition();

        if (format != null && format.length > 0) {
            res = res.and(table.FORMATID.in(format));
        }

//        if (status != null) {
//            res = res.and(table.WORKINGSTATUS.eq(MerchantWorkingStatuses.valueOf(status)));
//        }
        if (!isAdmin) res = res.and(table.WORKINGSTATUS.eq(MerchantWorkingStatuses.valueOf("Open")));
        var result = res
                .orderBy(table.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .intoGroups(table);

        return result
                .values()
                .stream()
                .map(r -> {
                    var merchant = r.into(table.fields())
                            .into(Merchant.class).get(0);
                    var attributeList = r.into(MerchantAttribute.class);
                    merchant.setAttributes(attributeList);

                    return merchant;
                }).collect(Collectors.toList());
    }

    @Override
    public void insertAttributesToMerchant(Merchant result) {
        var attribute =
                dslContext.select(MerchantAttributes.MERCHANT_ATTRIBUTES.ID)
                        .select(MerchantAttributes.MERCHANT_ATTRIBUTES.NAME)
                        .select(MerchantAttributes.MERCHANT_ATTRIBUTES.DRAFT)
                        .from(MerchantAttributes.MERCHANT_ATTRIBUTES
                                .leftJoin(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT)
                                .on(MerchantAttributes.MERCHANT_ATTRIBUTES.ID
                                        .eq(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.ATTRIBUTEID)))
                        .where(MerchantAttributeMerchant.MERCHANT_ATTRIBUTE_MERCHANT.MERCHANTSID
                                .eq(result.getId()))
                        .fetchInto(MerchantAttribute.class);

        result.setAttributes(attribute);
    }

    @Override
    public Optional<Merchant> findById(Long id) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(Merchant.class);
    }


    @Override
    public Optional<LoymaxMerchant> getLoymaxMerchant(String locationId) {
        return dslContext
                .selectFrom(table)
                .where(table.LOYMAXLOCATIONID.eq(locationId))
                .limit(1)
                .fetchOptionalInto(LoymaxMerchant.class);
    }
}

