package ru.sparural.engine.api.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDTO {
    Long id;
    @NotBlank(message = "Title is empty")
    @Size(max = 100, message = "The maximum length of a title is 100 characters")
    String title;
    @NotBlank(message = "Short Description is empty")
    @Size(max = 255, message = "The maximum length of shortDescription is 255 characters")
    String shortDescription;
    @Size(max = 255, message = "The maximum length of URL is 255 characters")
    String url;
    Boolean isPublic = false;
    Boolean draft = true;
    FileDto photo;
}
