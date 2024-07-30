package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;
import ru.sparural.engine.entity.enums.MerchantWorkingStatuses;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class Merchant {
    private Long id;
    private String title;
    private String address;
    private Double longitude;
    private Double latitude;
    private Long formatId;
    private String workingHoursFrom;
    private String workingHoursTo;
    private MerchantWorkingStatuses workingStatus;
    private List<MerchantAttribute> attributes;
    private String loymaxLocationId;
    private Boolean isPublic;
}
