package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchFilterDto {
    Integer offset;
    Integer limit;
    List<Long> role;
    List<Long> role_ne;
    Long attributeId;
    Long group;
    Long notinGroup;
    String search;
    Integer minAge;
    Integer maxAge;
    Long minRegistrationDate;
    Long maxRegistrationDate;
    String alphabetSort;
    String gender;
    Boolean hasEmail;
    Long counterId;
    Long counterMin;
    Long counterMax;
    Long currencyId;
    Long currencyMin;
    Long currencyMax;
    Long statusId;
}
