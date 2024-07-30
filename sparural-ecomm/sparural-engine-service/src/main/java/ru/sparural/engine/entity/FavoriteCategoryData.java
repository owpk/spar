package ru.sparural.engine.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;


@Data
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavoriteCategoryData {
    String acceptedDate;
    Long selectedPeriod;
    Long maxCount;
}
