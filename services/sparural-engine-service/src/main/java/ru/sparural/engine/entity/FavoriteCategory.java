package ru.sparural.engine.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavoriteCategory {
    Long id;
    String name;
    Long userId;
    String goodsGroupUID;
    Long startActiveDate;
    Long endActiveDate;
    Integer preferenceType;
    Integer preferenceValue;
    Boolean isPublic;
    //File photo;
    Boolean accepted;
    String loymaxId;
}
