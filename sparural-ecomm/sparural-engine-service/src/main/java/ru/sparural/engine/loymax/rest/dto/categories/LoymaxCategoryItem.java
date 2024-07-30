package ru.sparural.engine.loymax.rest.dto.categories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@Data
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxCategoryItem {
    String id;
    String name;
    String goodsGroupUID;
    String startActiveDate;
    String endActiveDate;
    Integer preferenceType;
    Integer preferenceValue;
    Boolean accepted;
}
