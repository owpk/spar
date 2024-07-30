package ru.sparural.engine.api.dto.main;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
public class GoodsDto {
    Long id;
    CategoryDto category;
    @JsonUnwrapped
    OfferDto offerDto;
    String title;
    String description;
    String shorDescription;
    Long begin;
    Long end;
    FileDto preview;
    FileDto photo;
    String discountLabel;
}
