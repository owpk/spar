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
public class PersonalOfferCreateDto {
    //    @NotNull
//    @NotBlank(message = "Please write attribute")
    @Size(max = 100, message = "The maximum length of an attribute is 100 characters")
    String attribute;
    //    @NotNull
//    @NotBlank(message = "Please write title")
    @Size(max = 255, message = "The maximum length of a title is 255 characters")
    String title;
    //    @NotBlank(message = "Please write description")
    @Size(max = 1000, message = "The maximum length of a description is 1000 characters")
    String description;
    @Min(value = 0, message = "Incorrect begin time")
//    @NotNull(message = "Please write date begin")
    Long begin;
    @Min(value = 0, message = "Incorrect end time")
//    @NotNull(message = "Please write date end")
    Long end;
    Boolean draft;
    Boolean isPublic;
}
