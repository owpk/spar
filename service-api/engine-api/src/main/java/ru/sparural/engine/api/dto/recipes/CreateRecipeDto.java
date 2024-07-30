package ru.sparural.engine.api.dto.recipes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRecipeDto {

    String title;
    String description;
    Integer calories;
    Integer proteins;
    Integer fats;
    Integer carbohydrates;
    Boolean draft;
    List<Long> attributes = Collections.emptyList();
    List<Long> goods = Collections.emptyList();
}
