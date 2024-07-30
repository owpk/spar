package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.ValidateCitySelect;
import ru.sparural.engine.api.validators.annotations.ValidateInfoScreen;

import java.util.List;

/**
 * @see ru.sparural.engine.api.validators.InfoScreenValidator
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidateInfoScreen
public class InfoScreenDto {

    Long id;
    FileDto photo;
    List<CityDto> cities;

    @ValidateCitySelect
    String citySelect;

    Boolean isPublic;

    Boolean draft;

    Long dateStart;

    Long dateEnd;
}
