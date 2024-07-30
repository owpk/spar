package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PushTokenReqDto {

    @Pattern(regexp = "(android|huawei|ios)",
            message = "This device type does not exist, possible values: android, ios, huawei")
    @NotBlank(message = "Please indicate the type of device")
    String deviceType;

    @Size(max = 255, message = "Maximum token length 255 characters")
    @NotBlank(message = "Please enter a token")
    String token;
}
