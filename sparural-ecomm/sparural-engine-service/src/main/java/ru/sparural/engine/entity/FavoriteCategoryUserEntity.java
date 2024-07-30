package ru.sparural.engine.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavoriteCategoryUserEntity {
    Long id;
    Long userId;
    Long categoryMonthId;
    Long categoryId;
    String name;
    Long startActiveDate;
    Long endActiveDate;
    Integer preferenceType;
    Integer preferenceValue;
    Boolean isPublic;
    Boolean accepted;
}
