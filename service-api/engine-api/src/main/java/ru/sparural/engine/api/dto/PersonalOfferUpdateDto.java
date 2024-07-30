package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalOfferUpdateDto {
    @Size(max = 100, message = "The maximum length of an attribute is 100 characters")
    String attribute;
    @Size(max = 255, message = "The maximum length of a title is 255 characters")
    String title;
    @Size(max = 1000, message = "The maximum length of a description is 1000 characters")
    String description;
    @Min(value = 0, message = "Incorrect begin time")
    Long begin;
    @Min(value = 0, message = "Incorrect end time")
    Long end;
    Boolean draft;
    Boolean isPublic;
}
