package ru.sparural.engine.api.dto.recipes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.goods.GoodsDto;

import java.util.Collections;
import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeDto {
    Long id;
    String title;
    String description;
    Integer calories;
    Integer proteins;
    Integer fats;
    Integer carbohydrates;
    FileDto preview;
    FileDto photo;
    Boolean draft;
    List<RecipeAttributesDto> attributes = Collections.emptyList();
    List<GoodsDto> goods = Collections.emptyList();
}
