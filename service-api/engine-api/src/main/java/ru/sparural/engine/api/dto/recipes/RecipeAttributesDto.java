package ru.sparural.engine.api.dto.recipes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.validators.annotations.ValidateRecipeAttribute;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@ValidateRecipeAttribute
public class RecipeAttributesDto {
    Long id;

    @Size(max = 100, message = "name field maximum length: 100")
    String name;

    Boolean showOnPreview;
    FileDto icon;
    Boolean draft;
}
