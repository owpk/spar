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
@Builder
@ToString
public class OfferDto {
    Long id;
    String title;
    String description;
    String shortDescription;
    Long begin;
    Long end;
    FileDto preview;
    FileDto photo;
    @JsonIgnore
    Long loymaxId;
    CounterOfferDto counter;
}