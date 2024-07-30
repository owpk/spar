package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantDto {

    @JsonProperty
    Long id;

    @Size(max = 100, message = "The maximum length of the store name is 100 characters")
    @NotNull(message = "Please enter the name of the store")
    String title;

    @Size(max = 255, message = "Maximum store address length 255 characters")
    @NotNull(message = "Please enter the address of the store")
    String address;

    @Min(value = -180, message = "Minimum value -180")
    @Max(value = 180, message = "Minimum value 180")
    @NotNull(message = "Please indicate the longitude")
    Double longitude;

    @Min(value = -90, message = "Minimum value -90")
    @Max(value = 90, message = "Minimum value 90")
    @NotNull(message = "Please indicate the latitude")
    Double latitude;

    @NotNull(message = "Specify the id of format")
    Long formatId;

    @Pattern(regexp = "^(?:[01][0-9]|2[0-3]):[0-5][0-9]$", message = "Wrong time format, should match HH:MM")
    @NotNull(message = "Specify working hours from")
    String workingHoursFrom;


    @Pattern(regexp = "^(?:[01][0-9]|2[0-3]):[0-5][0-9]$", message = "Wrong time format, should match HH:MM")
    @NotNull(message = "Specify working hours from")
    String workingHoursTo;

    @Pattern(regexp = "Open|OnRepair|Closed")
    @NotNull(message = "Specify working status")
    String workingStatus;

    @NotNull(message = "Specify store attributes")
    List<Long> attributes;

    @NotNull(message = "Specify id loymax location")
    String loymaxLocationId;

    Boolean isPublic;

}