package ru.sparural.engine.api.dto.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto {
    Long id;
    String name;
    Long userId;
    @JsonIgnore
    String goodsGroupUID;
   // @JsonIgnore
    Long startActiveDate;
    //@JsonIgnore
    Long endActiveDate;
    Integer preferenceType;
    Integer preferenceValue;
    Boolean isPublic;
    FileDto photo;
    Boolean accepted;
}
