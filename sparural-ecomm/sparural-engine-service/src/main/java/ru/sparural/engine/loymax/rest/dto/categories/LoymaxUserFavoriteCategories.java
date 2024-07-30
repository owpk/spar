package ru.sparural.engine.loymax.rest.dto.categories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxUserFavoriteCategories {
    String acceptedDate;
    Long selectedPeriod;
    Long maxCount;
    List<LoymaxCategoryItem> categories;
}
