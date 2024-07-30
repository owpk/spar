package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.delivery.ValidateDeliveryCreate;

import javax.validation.constraints.Size;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidateDeliveryCreate
public class DeliveryCreateDTO {
    //TODO add photo
    @Size(max = 100, message = "The maximum length of a title is 100 characters")
    String title;
    @Size(max = 255, message = "The maximum length of a shortDescription is 255 characters")
    String shortDescription;
    @Size(max = 255, message = "The maximum length of a url is 255 characters")
    String url;
    Boolean isPublic = false;
    Boolean draft = true;
}