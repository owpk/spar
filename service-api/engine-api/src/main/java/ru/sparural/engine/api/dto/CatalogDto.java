package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.Url;
import ru.sparural.engine.api.validators.annotations.ValidateCatalog;
import ru.sparural.engine.api.validators.annotations.ValidateCitySelect;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ValidateCatalog
public class CatalogDto {

    Long id;

    @Size(max = 255, message = "The maximum length of a directory name is 255 characters")
    String name;

    @ValidateCitySelect
    String citySelect;

    @Size(max = 255, message = "Maximum link length 255 characters")
    @Url
    String url;

    FileDto photo;
    Boolean draft;
    List<CityDto> cities;
}