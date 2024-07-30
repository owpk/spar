package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalOfferDto {
    Long id;
    String attribute;
    String title;
    String description;
    Long begin;
    Long end;
    Boolean isPublic;
    Boolean draft;
    FileDto preview;
    FileDto photo;
}
