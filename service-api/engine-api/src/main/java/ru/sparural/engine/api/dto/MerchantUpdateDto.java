package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantUpdateDto {
    @Size(max = 100, message = "The maximum length of the store name is 100 characters")
    String title;

    @Size(max = 255, message = "Maximum store address length 255 characters")
    String address;

    @Min(value = -180, message = "Minimum value -180")
    @Max(value = 180, message = "Minimum value 180")
    Double longitude;

    @Min(value = -90, message = "Minimum value -90")
    @Max(value = 90, message = "Minimum value 90")
    Double latitude;

    Long formatId;

    @Pattern(regexp = "^(?:[01][0-9]|2[0-3]):[0-5][0-9]$", message = "Wrong time format, should match MM:SS")
    String workingHoursFrom;


    @Pattern(regexp = "^(?:[01][0-9]|2[0-3]):[0-5][0-9]$", message = "Wrong time format, should match MM:SS")
    String workingHoursTo;

    @Pattern(regexp = "Open|OnRepair|Closed")
    String workingStatus;

    List<Long> attributes;

    //    @NotNull(message = "Specify id loymax location")
    String loymaxLocationId;

    Boolean isPublic;

}
