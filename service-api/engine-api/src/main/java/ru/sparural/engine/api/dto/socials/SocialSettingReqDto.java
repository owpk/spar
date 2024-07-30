package ru.sparural.engine.api.dto.socials;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocialSettingReqDto {

    Long id;

    @Size(max = 255, message = "Maximum Application ID length 255 characters")
    @NotBlank(message = "Specify the application ID")
    String appId;

    @Size(max = 255, message = "Maximum length of the application secret is 255 characters")
    @NotBlank(message = "Specify the application secret")
    String appSecret;
}
