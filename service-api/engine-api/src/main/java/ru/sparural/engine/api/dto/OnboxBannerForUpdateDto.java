package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.Url;
import ru.sparural.engine.api.validators.annotations.ValidateCitySelect;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @see ru.sparural.engine.api.validators.OnboxBannerValidator
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
//@ValidateOnboxBanner
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnboxBannerForUpdateDto {

    @JsonProperty
    Long id;

    @Min(value = 0, message = "minimum value 0")
    @Max(value = 100, message = "maximum value 100")
    Integer order;

    @ValidateCitySelect
    String citySelect;

    List<CityDto> cities;

    Boolean isPublic;

    Boolean draft;

    @Size(max = 50, message = "maximum length 50")
    String title;

    @Size(max = 255, message = "maximum length 255")
    String description;

    @Url
    @Size(max = 255, message = "maximum length 255, link")
    String url;

    @JsonProperty
    Long mobileNavigateTargetId;

    Long dateStart;

    Long dateEnd;

    FileDto photo;
}